package com.voting_system.authentication_service.dto;

import java.time.Instant;

import com.voting_system.authentication_service.model.VoterRegistrationStatus;

public record VoterRegistrationDto(
    String userId,
    String username,
    String email,
    boolean emailVerified,
    String firstName,
    String lastName,
    String matricula,
    boolean matriculaValid,
    boolean enabled,
    Instant registeredAt,
    VoterRegistrationStatus status
) {}
