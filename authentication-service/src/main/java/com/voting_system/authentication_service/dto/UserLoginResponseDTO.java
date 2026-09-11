package com.voting_system.authentication_service.dto;

import org.keycloak.representations.AccessTokenResponse;

public record UserLoginResponseDTO(
    String accessToken,
    String refreshToken,
    Long expiresIn,
    Long refreshExpiresIn,
    String tokenType
) {
    public static UserLoginResponseDTO from(AccessTokenResponse response) {
        return new UserLoginResponseDTO(
            response.getToken(),
            response.getRefreshToken(),
            response.getExpiresIn(),
            response.getRefreshExpiresIn(),
            response.getTokenType()
        );
    }
}
