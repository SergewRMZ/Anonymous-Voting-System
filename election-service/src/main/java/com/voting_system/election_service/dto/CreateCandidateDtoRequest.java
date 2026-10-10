package com.voting_system.election_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateCandidateDtoRequest (
    @NotBlank (message = "Name is required")
    @Size (max = 50, message = "Candidate name must not have more than 50 characters")
    String name,

    @NotBlank (message = "Last Name is required")
    @Size (max = 50, message = "Candidate lastname must not have more than 50 characters")
    String lastName,

    @NotBlank (message = "Description is required")
    @Size (max = 100, message = "Candidate description must not have more than 100 characters")
    String description
) {

}

