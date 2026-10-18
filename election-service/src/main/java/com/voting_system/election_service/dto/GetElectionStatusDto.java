package com.voting_system.election_service.dto;

import java.util.UUID;

import com.voting_system.election_service.domain.ElectionStatus;

public record GetElectionStatusDto (
    UUID electionId,
    ElectionStatus status
) {}