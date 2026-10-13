package com.voting_system.bulletin_board.config;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class InternalTokenValidator {
    private final String expectedToken;

    public InternalTokenValidator(@Value("${services.internal.token}") String expectedToken) {
        this.expectedToken = expectedToken;
    }

    public void validate(String presentedToken) {
        if (expectedToken.isBlank() || presentedToken == null
            || !MessageDigest.isEqual(
                expectedToken.getBytes(StandardCharsets.UTF_8),
                presentedToken.getBytes(StandardCharsets.UTF_8)
            )) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid internal service token");
        }
    }
}
