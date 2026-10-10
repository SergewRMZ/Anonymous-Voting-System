package com.voting_system.election_service.services.interfaces;

import java.util.List;
import java.util.UUID;

import com.voting_system.election_service.domain.ElectionModel;
import com.voting_system.election_service.dto.CreateElectionDtoRequest;
import com.voting_system.election_service.dto.ElectionDetailsDtoResponse;

public interface IElectionService {
    public ElectionModel createElection(CreateElectionDtoRequest createElectionDtoRequest);
    public ElectionModel publishElection(UUID electionId);
    public ElectionModel validateElection(UUID electionId);
    public ElectionModel activateElection(UUID electionId);
    public List<ElectionModel> getElections(List<String> roles);
    public ElectionDetailsDtoResponse getElectionDetails(UUID electionId, List<String> roles);
}
