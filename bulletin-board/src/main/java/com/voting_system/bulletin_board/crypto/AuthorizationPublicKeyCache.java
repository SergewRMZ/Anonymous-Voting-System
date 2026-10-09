package com.voting_system.bulletin_board.crypto;

import java.security.PublicKey;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

@Component 
public class AuthorizationPublicKeyCache {
    private final ConcurrentHashMap<UUID, PublicKey> keys = 
        new ConcurrentHashMap<>();

    public PublicKey getPublicKey(UUID authorizationKeysId) {
        return keys.get(authorizationKeysId);
    }

    public void put(UUID authorizationKeysId, PublicKey publicKey) {
        keys.put(authorizationKeysId, publicKey);
    }
}
