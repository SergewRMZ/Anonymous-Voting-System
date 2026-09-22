package com.voting_system.authentication_service.dto;

public record UserRegisterResponseDTO (
    String message,
    String userId
) {
    public static UserRegisterResponseDTO from(String message, String userId) {
        return new UserRegisterResponseDTO(message, userId);
    }

}
