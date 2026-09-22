package com.voting_system.tally_service.dto;

import com.voting_system.tally_service.model.KeyStatus;

import jakarta.validation.constraints.NotNull;

public record UpdateEncryptionKeyStatusDto (
    @NotNull 
    KeyStatus keyStatus
) {}
