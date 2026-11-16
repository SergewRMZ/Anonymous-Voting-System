package com.voting_system.election_service.dto;

import java.time.Instant;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateElectionDtoRequest(
    @NotBlank (message = "Election name is required")
    @Size (max = 150, message = "Election name must not have more than 150 characters")
    String name,

    @NotBlank (message = "Description is required")
    @Size (max = 1000, message = "Election description must not have more than 1000 characters")
    String description,

    @NotNull (message = "Start Date is required")
    @Future (message = "Start date must be in the future")
    Instant startDate,

    @NotNull (message = "End Date is required")
    @Future (message = "End date must be in the future")
    Instant endDate
) {}
