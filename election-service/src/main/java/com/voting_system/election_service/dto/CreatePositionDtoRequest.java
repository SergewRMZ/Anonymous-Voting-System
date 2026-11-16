package com.voting_system.election_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreatePositionDtoRequest(
    @NotBlank (message = "Position Name is required")
    @Size (max = 100, message = "Position name must not have more than 100 characters")
    String positionName
) {}
