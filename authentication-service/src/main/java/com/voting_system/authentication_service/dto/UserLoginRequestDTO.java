package com.voting_system.authentication_service.dto;

import jakarta.validation.constraints.NotBlank;

public record UserLoginRequestDTO(
    @NotBlank(message = "Username is required")
    String username,

    @NotBlank(message = "Password is required")
    String password
) {}
