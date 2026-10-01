package com.voting_system.election_service.services.interfaces;

import java.util.List;
import java.util.UUID;

import com.voting_system.election_service.domain.ElectionModel;
import com.voting_system.election_service.dto.CreateElectionDtoRequest;
import com.voting_system.election_service.dto.ElectionDetailsDtoResponse;

public interface IElectionService {
    public ElectionModel createElection(CreateElectionDtoRequest createElectionDtoRequest);
    public List<ElectionModel> getElections();
    public ElectionDetailsDtoResponse getElectionDetails(UUID electionId);
}
