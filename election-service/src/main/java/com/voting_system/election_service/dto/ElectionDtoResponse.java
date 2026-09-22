package com.voting_system.election_service.dto;

import java.time.Instant;
import java.util.UUID;

import com.voting_system.election_service.domain.ElectionModel;
import com.voting_system.election_service.domain.ElectionStatus;
public record ElectionDtoResponse(
    UUID electionId,
    String name,
    Instant startDate,
    Instant endDate,
    ElectionStatus status
) {
    public static ElectionDtoResponse from(ElectionModel electionModel) {
        return new ElectionDtoResponse(
            electionModel.getId(), 
            electionModel.getName(), 
            electionModel.getStartDate(), 
            electionModel.getEndDate(), 
            electionModel.getStatus());
    }

}
