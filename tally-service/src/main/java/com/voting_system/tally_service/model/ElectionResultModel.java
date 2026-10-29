package com.voting_system.tally_service.model;

import java.time.Instant;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter 
@AllArgsConstructor 
@NoArgsConstructor 
@Builder 
public class ElectionResultModel {
    private UUID id;
    private UUID electionId;
    private UUID electionPositionId;
    private UUID candidacyId;
    private int result;
    private Instant createdAt;
}
