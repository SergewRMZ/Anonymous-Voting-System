package com.voting_system.bulletin_board.crypto;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class CryptoUtils {
    public static byte[] sha3_384(String data) {
        if(data == null) {
            throw new IllegalArgumentException("Data cannot be null");
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA3-384");
            return digest.digest(data.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new IllegalStateException("SHA-384 algorithm is not available", e);
        }
    }

    public static boolean isSameDigest(byte[] receivedDigest, byte[] calculatedDigest) {
        return MessageDigest.isEqual(receivedDigest, calculatedDigest);
    }
}
