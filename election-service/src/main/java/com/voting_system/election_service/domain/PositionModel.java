package com.voting_system.election_service.domain;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter 
@AllArgsConstructor 
@Builder 
public class PositionModel {
    private UUID id;
    private UUID electionId;
    private String positionName;
}
