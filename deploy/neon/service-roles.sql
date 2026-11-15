-- Un rol y un esquema por servicio en la base voting_system (Neon).
-- Cada rol es dueño solo de su esquema: crea, lee y escribe sus tablas y no tiene
-- ningún permiso sobre los esquemas de los demás servicios.
--
-- Cómo usarlo (una sola vez):
--   1. En Neon, abre el SQL Editor del proyecto del sistema con la base voting_system.
--   2. Reemplaza cada <CONTRASEÑA_...> por la misma contraseña que pongas en .env.prod.
--      Usa solo letras y números (por ejemplo: openssl rand -hex 24) para no romper
--      las comillas del SQL ni la URL JDBC.
--   3. Ejecuta todo el script y, al terminar, borra esta consulta del historial del editor.
--
-- Los roles se crean con SQL a propósito: Neon agrega a neon_superuser los roles creados
-- desde su consola, CLI o API, y ese rol puede leer y escribir todas las tablas.
-- Los creados con SQL no reciben esa membresía (https://neon.com/docs/manage/roles).
-- El rol dueño del proyecto (creado en la consola) solo se usa para ejecutar este script
-- y administrar; ningún servicio se conecta con él.
-- Si cambias los nombres de los roles, cámbialos también en .env.prod (*_DB_USER).

CREATE ROLE authz_svc    LOGIN PASSWORD '<CONTRASEÑA_AUTHZ>';
CREATE ROLE election_svc LOGIN PASSWORD '<CONTRASEÑA_ELECTION>';
CREATE ROLE tally_svc    LOGIN PASSWORD '<CONTRASEÑA_TALLY>';
CREATE ROLE bulletin_svc LOGIN PASSWORD '<CONTRASEÑA_BULLETIN>';

-- authorization-service: authorization_keys, blind_signatures (identidad del votante)
CREATE SCHEMA authz    AUTHORIZATION authz_svc;
-- election-service: elections, positions, candidates, candidate_photos, candidacies...
CREATE SCHEMA election AUTHORIZATION election_svc;
-- tally-service: encryption_keys, election_results
CREATE SCHEMA tally    AUTHORIZATION tally_svc;
-- bulletin-board: votes (votos cifrados, sin identidad)
CREATE SCHEMA bulletin AUTHORIZATION bulletin_svc;

-- Las tablas sin esquema explícito se crean y se buscan en el esquema propio.
ALTER ROLE authz_svc    SET search_path = authz;
ALTER ROLE election_svc SET search_path = election;
ALTER ROLE tally_svc    SET search_path = tally;
ALTER ROLE bulletin_svc SET search_path = bulletin;

-- Solo el dueño y los cuatro servicios pueden conectarse a esta base.
REVOKE ALL ON DATABASE voting_system FROM PUBLIC;
GRANT CONNECT ON DATABASE voting_system TO authz_svc, election_svc, tally_svc, bulletin_svc;

-- Nadie crea objetos en el esquema público.
REVOKE CREATE ON SCHEMA public FROM PUBLIC;

-- Comprobación: cada esquema con su dueño.
SELECT nspname AS esquema, pg_get_userbyid(nspowner) AS dueno
FROM pg_namespace
WHERE nspname IN ('authz', 'election', 'tally', 'bulletin')
ORDER BY nspname;
