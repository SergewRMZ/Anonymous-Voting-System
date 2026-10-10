package com.voting_system.election_service.domain;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter 
@NoArgsConstructor 
@AllArgsConstructor 
@Builder 
public class ElectionPositionModel {
    private UUID id;
    private UUID electionId;
    private UUID positionId;
}
