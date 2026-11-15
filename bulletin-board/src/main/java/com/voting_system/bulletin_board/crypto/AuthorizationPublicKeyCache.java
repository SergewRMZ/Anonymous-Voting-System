package com.voting_system.bulletin_board.crypto;

import java.security.PublicKey;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

/**
 * Clave pública de autorización de cada elección, tal como la entrega authorization-service.
 *
 * Se indexa por elección y no por el authorizationKeysId que manda el cliente: así el cliente
 * no puede decidir con qué clave se verifica su voto, y la caché no crece con IDs inventados
 * (solo hay una entrada por elección que haya recibido votos).
 */
@Component
public class AuthorizationPublicKeyCache {

    public record ElectionKey(UUID authorizationKeysId, PublicKey publicKey) {}

    private final ConcurrentHashMap<UUID, ElectionKey> keys = new ConcurrentHashMap<>();

    public Optional<ElectionKey> get(UUID electionId) {
        return Optional.ofNullable(keys.get(electionId));
    }

    public void put(UUID electionId, UUID authorizationKeysId, PublicKey publicKey) {
        keys.put(electionId, new ElectionKey(authorizationKeysId, publicKey));
    }
}
