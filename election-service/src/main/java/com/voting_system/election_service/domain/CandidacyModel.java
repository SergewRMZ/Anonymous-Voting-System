package com.voting_system.election_service.domain;

import java.time.Instant;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter 
@NoArgsConstructor 
@AllArgsConstructor 
@Builder 
public class CandidacyModel {
    private UUID id;
    private UUID candidateId;
    private UUID electionPositionId;
    private Instant createdAt;
}
