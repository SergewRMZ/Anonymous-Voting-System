package com.voting_system.bulletin_board.crypto;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

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

    public static byte[] concatenate(byte[] data, byte[] randomizer) {
        byte[] result = new byte[data.length + randomizer.length];
        System.arraycopy(data, 0, result, 0, data.length);
        System.arraycopy(randomizer, 0, result, data.length, randomizer.length);
        return result;
    }

    public static boolean isSameDigest(byte[] receivedDigest, byte[] calculatedDigest) {
        return MessageDigest.isEqual(receivedDigest, calculatedDigest);
    }

    public static byte[] base64UrlToBytes(String data) {
        return Base64.getUrlDecoder().decode(data);
    }

    public static byte[] base64ToBytes(String data) {
        return Base64.getDecoder().decode(data);
    }

    public static String bytesToBase64(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }
}
