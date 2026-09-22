package com.voting_system.election_service.dto;

import java.time.Instant;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateElectionDtoRequest(
    @NotBlank (message = "Election name is required")
    String name,

    @NotBlank (message = "Description is required")
    String description,

    @NotNull (message = "Start Date is required")
    @Future (message = "Start date must be in the future")
    Instant startDate,

    @NotNull (message = "End Date is required")
    @Future (message = "End date must be in the future")
    Instant endDate
) {}
