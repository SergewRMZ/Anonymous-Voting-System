package com.voting_system.tally_service.crypto;

import java.util.Base64;

public class CryptoUtils {
    public static byte[] base64ToBytes(String data) {
        return Base64.getDecoder().decode(data);
    }

    public static String bytesToBase64(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }
}
