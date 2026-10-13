# Flujo de admisión de votos y escrutinio homomórfico

Este documento describe el flujo implementado entre `api-gateway`, `election-service`, `bulletin-board` y `tally-service`. Los diagramas están en PlantUML para poder incluirlos o renderizarlos en la documentación técnica.

## 1. Componentes y responsabilidades

```plantuml
@startuml
title Componentes del flujo de votación y escrutinio
left to right direction

actor Votante
actor Autoridad
component "API Gateway" as Gateway
component "Election Service" as Election
component "Bulletin Board" as Board
component "Tally Service" as Tally
database "Base de elecciones" as ElectionsDB
database "Base del bulletin board\n(votos + compuerta)" as BoardDB
database "Base de claves" as KeysDB
file "Clave privada por elección\n(PEM local)" as PrivateKey

Votante --> Gateway : Enviar voto cifrado
Autoridad --> Gateway : Cerrar / solicitar tally /\nfinalizar tras revisar resultados
Gateway --> Board : POST /api/bulletin-board/elections/{id}/votes
Gateway --> Election : PATCH /api/election-service/elections/{id}/close
Gateway --> Tally : POST /api/tally-service/elections/{id}/tally
Gateway --> Election : PATCH /api/election-service/elections/{id}/finish

Board --> Election : GET /internal/elections/{id}/status\nX-Internal-Token
Election --> Board : POST /internal/elections/{id}/close\nX-Internal-Token
Tally --> Election : GET /internal/elections/{id}/status\nX-Internal-Token
Tally --> Board : GET /internal/elections/{id}/votes\nX-Internal-Token

Election --> ElectionsDB
Board --> BoardDB
Tally --> KeysDB : Leer clave pública y estado
Tally --> PrivateKey : Leer clave privada para descifrar agregado
@enduml
```

Las rutas `/internal/**` son llamadas entre servicios y requieren el encabezado `X-Internal-Token`. Configurar `INTERNAL_SERVICE_TOKEN` con un valor no vacío e idéntico en los servicios participantes. En despliegue, las URLs de servicio deben apuntar a direcciones privadas. Este token compartido es la configuración actual; una integración con clientes confidenciales de Keycloak puede reemplazarlo para utilizar credenciales y permisos distintos por servicio.

## 2. Recepción de un voto

El bulletin board consulta la elegibilidad antes de hacer trabajo criptográfico y vuelve a consultarla antes de persistir. La compuerta por elección se bloquea dentro de la misma transacción que guarda el voto: esto coordina la última comprobación y la escritura con la operación de cierre.

```plantuml
@startuml
title Admisión y registro de un voto
actor Votante
participant "API Gateway" as Gateway
participant "Bulletin Board" as Board
participant "Election Service" as Election
database "Board DB" as DB
participant "Authorization Service" as Authorization

Votante -> Gateway : POST /api/bulletin-board/elections/{id}/votes\n{ encryptedVote, randomizer, digest, signature }
Gateway -> Board : Reenvía la solicitud

Board -> Election : GET /internal/elections/{id}/status
Election --> Board : status + acceptingVotes
alt no está ACTIVE, fuera de fechas o election-service falla
    Board --> Gateway : Rechazar; no se guarda el voto
    Gateway --> Votante : Error
else elección acepta votos
    Board -> DB : Comprobar firma duplicada
    alt firma ya registrada
        Board --> Gateway : 409 Duplicate Vote
        Gateway --> Votante : Conflicto
    else voto no duplicado
        Board -> Authorization : Obtener clave pública de autorización si no está en caché
        Authorization --> Board : Clave pública RSA
        Board -> Board : Canonicalizar el voto y verificar digest y firma
        alt digest o firma inválidos
            Board --> Gateway : Rechazar voto
            Gateway --> Votante : Error de validación
        else validación criptográfica correcta
            group Transacción de admisión y escritura
                Board -> DB : Crear/obtener compuerta y bloquear fila de electionId
                alt compuerta cerrada
                    Board -> DB : Rollback
                    Board --> Gateway : 409 elección no acepta votos
                else compuerta abierta
                    Board -> Election : Consultar estado y fechas de nuevo
                    Election --> Board : acceptingVotes
                    alt elección ya no acepta votos
                        Board -> DB : Rollback
                        Board --> Gateway : 409 elección no acepta votos
                    else sigue aceptando
                        Board -> DB : Comprobar duplicado de nuevo y guardar voto
                        Board -> DB : Commit y liberar bloqueo
                        Board --> Gateway : 201 Created + voteId
                    end
                end
            end
        end
    end
end
@enduml
```

`acceptingVotes` solo es verdadero cuando el estado es `ACTIVE` y la hora está en el intervalo semiabierto `[startDate, endDate)`. Si la elección no existe, la llamada devuelve error y no se registra el voto. Si el servicio de elecciones no responde, la solicitud falla cerrada: el bulletin board tampoco lo acepta.

## 3. Cierre coordinado de la elección

`CLOSING` no es un estado de votación ni un estado final. Es la barrera durable que se escribe en election-service **antes** de contactar al bulletin board: desde ese momento las comprobaciones de elegibilidad no aceptan nuevos votos. Después se cierra la compuerta local de admisión.

```plantuml
@startuml
title Cierre coordinado y frontera de votos aceptados
actor Autoridad
participant "API Gateway" as Gateway
participant "Election Service" as Election
database "Election DB" as ElectionsDB
participant "Bulletin Board" as Board
database "Board DB" as BoardDB

Autoridad -> Gateway : PATCH /api/election-service/elections/{id}/close
Gateway -> Election : Solicitar cierre
Election -> ElectionsDB : Cambiar ACTIVE a CLOSING
ElectionsDB --> Election : Guardado
note over Election,Board
Desde CLOSING, las consultas de elegibilidad
responden acceptingVotes=false.
end note

Election -> Board : POST /internal/elections/{id}/close
Board -> BoardDB : Crear/obtener y bloquear fila de compuerta
note over BoardDB
Una transacción de voto bloquea la misma fila
hasta que termina la validación final y el guardado.
end note
alt hay un voto que ya obtuvo el bloqueo
    BoardDB --> Board : Espera a que termine la transacción del voto
    BoardDB --> Board : Adquirir bloqueo al liberarse
else no hay voto en curso con el bloqueo
    BoardDB --> Board : Adquirir bloqueo
end
Board -> BoardDB : Marcar compuerta closed=true y commit
Board --> Election : 204 No Content
Election -> ElectionsDB : Cambiar CLOSING a CLOSED
ElectionsDB --> Election : Guardado
Election --> Gateway : Elección CLOSED
Gateway --> Autoridad : 200 OK

opt error al llamar al bulletin board o guardar el cierre
    note over Election,ElectionsDB
    Se conserva CLOSING; no se debe reabrir la admisión.
    La autoridad puede reintentar PATCH /close.
    end note
end
@enduml
```

La compuerta y la escritura del voto usan la misma base de datos y la misma fila bloqueada por elección. Si un voto ya pasó la consulta inicial a election-service cuando empezó el cierre, la segunda consulta —después de obtener el bloqueo— detecta `CLOSING` y aborta. Si el voto obtuvo primero el bloqueo y pasó esa segunda consulta, el cierre espera a que la transacción termine; por tanto, ese voto forma parte del conjunto cerrado. Los votos de otras elecciones utilizan otras filas y no se bloquean entre sí.

El cierre es reintentable: si la elección ya está `CLOSING`, se repite la orden de cerrar la compuerta y luego se completa la transición a `CLOSED`. Si ya está `CLOSED`, la operación no necesita volver a cerrarla.

## 4. Tally homomórfico

El endpoint de tally es `POST /api/tally-service/elections/{id}/tally` y está restringido a autoridades en el gateway. El servicio exige estado `CLOSED` (o `FINISHED` para permitir recalcular) y clave de tally activa. El tally devuelve los conteos y **no cambia** el estado de la elección. Una vez que la autoridad electoral revisa y acepta los resultados, puede finalizarla con `PATCH /api/election-service/elections/{id}/finish`. Ese endpoint exige el rol `AUTHORITY` en el gateway, cambia una elección `CLOSED` a `FINISHED` y es idempotente si ya estaba `FINISHED`.

```plantuml
@startuml
title Lectura, suma homomórfica y descifrado del tally
actor Autoridad
participant "API Gateway" as Gateway
participant "Tally Service" as Tally
participant "Election Service" as Election
participant "Bulletin Board" as Board
database "Board DB" as BoardDB
database "Key DB" as KeysDB
file "Clave privada PEM" as PrivateKey

Autoridad -> Gateway : POST /api/tally-service/elections/{id}/tally
Gateway -> Tally : Ejecutar tally
Tally -> Election : GET /internal/elections/{id}/status
Election --> Tally : status
alt estado no es CLOSED ni FINISHED
    Tally --> Gateway : 409 Election Not Closed
    Gateway --> Autoridad : Rechazo
else cerrado o tally ya finalizado
    Tally -> KeysDB : Consultar clave de la elección
    KeysDB --> Tally : Clave pública + estado
    alt clave no encontrada o no ACTIVE
        Tally --> Gateway : Error de clave
    else clave activa
        Tally -> Board : GET /internal/elections/{id}/votes
        Board -> BoardDB : Leer votos cifrados
        Board --> Tally : Lista de votos
        Tally -> Tally : Decodificar c1,c2 y validar puntos secp256k1
        Tally -> Tally : Agrupar por (electionPositionId, candidacyId)
        Tally -> Tally : Sumar c1 y c2 de cada grupo
        Tally -> PrivateKey : Leer escalar privado x
        PrivateKey --> Tally : x
        Tally -> Tally : M = sum(c2) - x * sum(c1)
        Tally -> Tally : Resolver M = count * G,\n0 <= count <= ciphertexts del grupo
        alt punto inválido o resultado fuera del rango
            Tally --> Gateway : 422 Invalid Ciphertext
        else resultados válidos
            Tally --> Gateway : 200 + conteos por cargo/candidatura
            Gateway --> Autoridad : Resultados y cantidad de votos procesados
            opt autoridad revisa y acepta resultados
                Autoridad -> Gateway : PATCH /api/election-service/elections/{id}/finish
                Gateway -> Election : Solicitar finalización\n(rol AUTHORITY)
                Election -> Election : Validar estado CLOSED\n y cambiar a FINISHED
                Election --> Gateway : 200 + estado FINISHED
                Gateway --> Autoridad : Elección finalizada
            end
        end
    end
end
@enduml
```

### Contrato criptográfico esperado

Para cada candidatura/cargo, `vote.c1` y `vote.c2` son puntos de la curva `secp256k1`, serializados en SEC1 y codificados en Base64. El cifrado debe ser ElGamal aditivo:

```text
Y = x * G                         clave pública
m ∈ {0, 1}                        voto binario
P = m * G                         punto que representa el voto
c1 = r * G
c2 = P + r * Y

sum(c1) = Σ c1
sum(c2) = Σ c2
M = sum(c2) - x * sum(c1)         punto agregado descifrado
M = count * G                     count ∈ [0, cantidad de ciphertexts]
```

La propiedad homomórfica permite sumar componentes de los ciphertexts sin conocer los votos individuales. El código no descifra cada ballot: descifra un punto agregado por candidatura y busca el múltiplo de `G` dentro de un rango acotado por el número de ciphertexts del grupo.

## 5. Límites y consideraciones antes de producción

- El tally comprueba que los puntos recibidos se puedan decodificar y sean puntos válidos de la curva, y rechaza resultados que no correspondan a un conteo dentro del rango permitido.
- El flujo **asume** que el productor cifra solamente `0` o `1`. No verifica todavía una prueba de conocimiento cero que pruebe la restricción de bit ni que el voto cumpla la regla electoral de selección única. Una firma de autorización valida la firma sobre el voto, pero no reemplaza esas pruebas criptográficas.
- El formato de ballots actual agrupa ciphertexts presentes por cargo y candidatura. Asegúrense de que el cliente construya un ciphertext de 0/1 para cada opción según el modelo electoral, para que el tally incluya también los ceros.
- La clave privada actual se guarda como archivo PEM local y el tally usa un único custodio. Este diseño no es umbral: restringir el endpoint de tally y proteger el almacenamiento de claves es esencial. El descifrado umbral queda como evolución posterior.
- El endpoint de finalización registra la decisión de la autoridad y valida la transición de estado, pero no almacena ni verifica una copia de los resultados del tally. La autoridad debe ejecutar el tally, revisar los resultados obtenidos y luego solicitar explícitamente la finalización. La elección permanece `CLOSED` hasta esa decisión.

## 6. Estados: elección y claves

### Ciclo de vida de una elección

```plantuml
@startuml
title Estados de una elección y responsable de cada transición

[*] --> DRAFT : Crear elección\nADMIN
DRAFT --> PUBLISHED : PATCH /publish\nADMIN
PUBLISHED --> VERIFIED : PATCH /validate\nservicio de elección*
VERIFIED --> ACTIVE : PATCH /activate\nAUTHORITY + ventana de fechas
ACTIVE --> CLOSING : PATCH /close\nAUTHORITY
CLOSING --> CLOSED : Compuerta BB cerrada\nElection Service completa
CLOSED --> FINISHED : PATCH /finish\nAUTHORITY

note right of DRAFT
Se crea con fechas válidas.
end note
note right of CLOSING
Estado persistente de coordinación.
Rechaza nuevos votos mientras
se congela el bulletin board.
end note
note right of FINISHED
La autoridad declara concluido
el proceso tras revisar resultados.
end note
@enduml
```

`ADMIN` crea y publica en las reglas del API Gateway. `AUTHORITY` activa, cierra y finaliza. La transición `PUBLISHED → VERIFIED` existe en el servicio y tiene endpoint `PATCH /{electionId}/validate`; el gateway actual no contiene una regla específica para ese endpoint, por lo que cae en `.anyExchange().authenticated()`. El asterisco indica esta diferencia entre la ruta y el control de rol actual.

`ACTIVE → CLOSING → CLOSED` son dos pasos porque interviene el bulletin board. `CLOSING` ya bloquea la admisión a través de la consulta autoritativa, y `CLOSED` solo se persiste después de que el bulletin board confirme que cerró la compuerta. `CLOSED → FINISHED` es una decisión posterior y explícita de la autoridad. El sistema no pasa automáticamente una elección a `FINISHED` al completar el cálculo.

### Ciclo de vida de claves

```plantuml
@startuml
title Estados de claves de autorización y de cifrado de tally

state "Clave pública de autorización\n(RSA BSSA)" as AuthKey {
    [*] --> AuthCreated : POST /keys\nAUTHORITY
    AuthCreated --> AuthActive : PATCH /keys/activate\nAUTHORITY
    AuthActive --> AuthExpired : expire() en el dominio\n(sin endpoint expuesto)
}

--

state "Clave de cifrado de tally\n(ElGamal secp256k1)" as TallyKey {
    [*] --> TallyCreated : POST /encryption-keys\nAUTHORITY
    TallyCreated --> TallyActive : PATCH /encryption-keys/status\nkeyStatus=ACTIVE\nAUTHORITY
    TallyActive --> TallyExpired : PATCH /encryption-keys/status\nkeyStatus=EXPIRED\nAUTHORITY
}

note right of AuthActive
El cliente la obtiene por GET /keys/public.
El Authorization Service usa la privada
para firmar ciegamente.
end note
note right of TallyActive
El cliente obtiene la pública para cifrar.
Tally usa la privada para descifrar
el agregado, nunca cada voto individual.
end note
@enduml
```

Las claves de autorización y las claves de cifrado de tally son distintas y no deben confundirse. Ambas tienen estados de dominio `CREATED`, `ACTIVE`, `EXPIRED`. La clave pública RSA activa se sirve por `GET /api/authorization-service/elections/{id}/keys/public`; la clave pública ElGamal se sirve por `GET /api/tally-service/elections/{id}/encryption-keys/public`. La clave privada RSA se conserva en Authorization Service y la privada ElGamal se conserva actualmente en un PEM local de Tally Service.

Los endpoints públicos actuales de administración permiten crear y activar las claves; el estado de clave de tally también se puede cambiar a `EXPIRED`. Para claves RSA existe `expire()` en el modelo, pero no hay endpoint para invocarlo. La activación de elección no verifica automáticamente que ambas claves estén creadas y activas: el operador debe completar este pre-requisito durante la preparación.

## 7. Protocolo de autorización y firma ciega

El siguiente diagrama presenta el **contrato criptográfico esperado para el cliente**, junto con las operaciones del servidor que sí están implementadas. No hay código de aplicación cliente en este repositorio que implemente el cifrado ElGamal, la preparación PSS ciega ni el descegado; esos pasos del lado cliente se muestran como protocolo requerido, no como comportamiento ya verificado en el repositorio.

```plantuml
@startuml
title Preparación del voto, firma RSA ciega y validación en bulletin board
actor Votante
participant "Aplicación cliente" as Client
participant "API Gateway" as Gateway
participant "Tally Service" as Tally
participant "Authorization Service" as Auth
participant "Bulletin Board" as Board
participant "Election Service" as Election

Votante -> Client : Autenticarse y seleccionar opciones
Client -> Gateway : GET /api/tally-service/elections/{id}/encryption-keys/public
Gateway -> Tally : Solicitar clave ElGamal pública
Tally --> Gateway : publicKey Y, electionId, keyStatus
Gateway --> Client : Clave pública de cifrado

Client -> Gateway : GET /api/authorization-service/elections/{id}/keys/public
Gateway -> Auth : Solicitar clave RSA pública activa
Auth --> Gateway : publicKey (n,e), authorizationKeysId
Gateway --> Client : Clave pública RSA + identificador

group Preparación local del voto (cliente)
    Client -> Client : Crear EncryptedVote con authorizationKeysId
    loop Cada cargo y candidatura
        Client -> Client : Representar opción como m ∈ {0,1}
        Client -> Client : P = m*G; elegir nonce secreto r
        Client -> Client : c1 = r*G; c2 = P + r*Y
        Client -> Client : Codificar c1,c2 como SEC1 Base64
    end
    Client -> Client : Canonicalizar JSON de EncryptedVote
    Client -> Client : digest = SHA3-384(canonicalVote)
    Client -> Client : Elegir randomizer aleatorio
    Client -> Client : message = randomizer || digest
    Client -> Client : Preparar representante RSA-PSS(message)\ny cegarlo con factor secreto
end

Client -> Gateway : POST /api/authorization-service/elections/{id}/blind-signature\n{ blindedMessage } + JWT de VOTER
Gateway -> Auth : Solicitar firma ciega
Auth -> Auth : Comprobar si usuario ya solicitó firma\n(comportamiento de persistencia pendiente)
Auth -> Auth : Exigir clave RSA activa
Auth -> Auth : Aplicar operación RSA privada al representante cegado
Auth --> Gateway : blindSignature
Gateway --> Client : Firma RSA cegada
Client -> Client : Descegar la firma con el factor privado
note over Client
El mensaje firmado debe ser exactamente
randomizer || digest para coincidir
con la verificación del bulletin board.
end note

Client -> Gateway : POST /api/bulletin-board/elections/{id}/votes\n{ encryptedVote, randomizer, digest, signature }
Gateway -> Board : Enviar voto
Board -> Election : GET /internal/elections/{id}/status
Election --> Board : acceptingVotes
Board -> Board : Canonicalizar EncryptedVote y recalcular SHA3-384
Board -> Board : Comprobar digest recibido
Board -> Board : Verificar RSASSA-PSS(signature,\nrandomizer || calculatedDigest, publicKey)
Board -> Board : Comprobar admisión y compuerta de cierre
Board -> Board : Persistir voto cifrado
Board --> Gateway : 201 + voteId
Gateway --> Client : Confirmación de registro
@enduml
```

### Interpretación de los datos y límites actuales

1. **Cifrado del voto.** Para cada opción, el cliente cifra `m ∈ {0,1}` como punto `P=mG` y produce el par ElGamal `(c1,c2)`. El randomness `r` debe ser nuevo e impredecible para cada ciphertext. La API guarda las cadenas, no cifra por sí misma.
2. **Digest.** El bulletin board serializa `EncryptedVote`, lo canonicaliza y calcula `SHA3-384`. `EncryptedVote` incluye `authorizationKeysId` y posiciones/candidaturas; el digest debe cubrir exactamente esa estructura canonicalizada. `randomizer` no forma parte del `EncryptedVote`.
3. **Mensaje firmado.** La verificación implementada en bulletin board comprueba una firma `RSASSA-PSS` con SHA3-384 sobre la concatenación binaria `randomizer || digest`. El orden y codificación deben coincidir en el cliente.
4. **Firma ciega.** El cliente obtiene la firma de un endpoint autenticado con rol `VOTER`; la idea es que Authorization Service firme un representante cegado sin conocer el voto y que el cliente descegue la respuesta. La solicitud de firma está asociada al usuario autenticado y a la elección.
5. **Autorización de clave.** En el bulletin board, `authorizationKeysId` identifica la clave que se usará para validar; si la clave no está en caché, obtiene la clave pública activa de Authorization Service, reconstruye RSA a partir de `(n,e)` y la cachea.
6. **Registro anónimo.** El bulletin board persiste ciphertext, randomizer, digest y firma; no necesita conocer la identidad del votante para validar la firma. Authorization Service sí conoce quién pidió la firma ciega.

**Pendiente importante antes de confiar en la unicidad de la firma ciega:** `BlindSignatureService` comprueba si existe una firma ciega previa para el par usuario/elección, pero la llamada a `blindSignatureRepositoryPort.save(model)` está comentada. Por tanto, con el código actual esa comprobación no deja persistido el registro y no garantiza realmente “una firma por votante y elección”. Hay que persistir de forma transaccional y respaldar con una restricción única en base de datos.

La verificación del bulletin board usa PSS sobre `randomizer || digest`, mientras que Authorization Service aplica la primitiva RSA privada sin padding a `blindedMessage`. Esto es compatible únicamente si el cliente realiza el padding/encoding PSS antes del cegado y el descegado correcto conforme a un esquema RSA blind signature compatible. El cliente no está incluido aquí y esa compatibilidad extremo a extremo aún no está demostrada por tests de protocolo; deben agregarse vectores de interoperabilidad antes de producción. No enviar el digest sin PSS-encoding como `blindedMessage`.

También hay que revisar la gestión de la clave RSA en Authorization Service antes de soportar varias elecciones activas: `RsaBssaService` mantiene un único `KeyPair` mutable como singleton y `loadKeys` no reemplaza una clave que ya está cargada. El identificador de clave que viaja en el voto no selecciona explícitamente el par privado usado por la operación de firma. El diseño objetivo debe resolver claves privadas por `authorizationKeysId`/elección y cargar la correcta para cada solicitud.
