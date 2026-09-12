package com.voting_system.authentication_service.dto;

public record UserRegisterResponseDTO (
    String message
) {
    public static UserRegisterResponseDTO from(String message) {
        return new UserRegisterResponseDTO(message);
    }

}
