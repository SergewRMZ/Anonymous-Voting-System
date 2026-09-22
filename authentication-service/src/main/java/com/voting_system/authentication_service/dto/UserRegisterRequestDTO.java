package com.voting_system.authentication_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRegisterRequestDTO (
    @NotBlank (message = "Username is required")
    String username,

    @NotBlank (message = "Email is required")
    @Email (message = "Email format is invalid")
    String email,

    @NotBlank (message = "Password is required")
    @Size (min = 6, message = "Password length must be at least 6 characters long")
    String password,

    String firstName,
    String lastName
) {}
