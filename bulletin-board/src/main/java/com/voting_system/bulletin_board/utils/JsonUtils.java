package com.voting_system.bulletin_board.utils;

import org.erdtman.jcs.JsonCanonicalizer;
import org.springframework.stereotype.Component;

/**
 * Utilidad para canonicalizar el JSON de acuerdo 
 * con el estándar RFC 8785.
 * JsonUtils
 */
public class JsonUtils {
    public static String canonicalize(String json) {
        try {
            JsonCanonicalizer canonicalizer = new JsonCanonicalizer(json);
            return canonicalizer.getEncodedString();
        } catch (Exception e) {
            throw new RuntimeException("Error al canonicalizar el JSON");
        }
    }
}
