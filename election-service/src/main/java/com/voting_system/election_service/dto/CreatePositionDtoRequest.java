package com.voting_system.election_service.dto;

import jakarta.validation.constraints.NotBlank;

public record CreatePositionDtoRequest(
    @NotBlank (message = "Position Name is required")
    String positionName
) {}
