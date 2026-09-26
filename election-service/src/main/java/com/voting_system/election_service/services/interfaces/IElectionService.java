package com.voting_system.election_service.services.interfaces;

import java.util.List;

import com.voting_system.election_service.domain.ElectionModel;
import com.voting_system.election_service.dto.CreateElectionDtoRequest;

public interface IElectionService {
    public ElectionModel createElection(CreateElectionDtoRequest createElectionDtoRequest);
    public List<ElectionModel> getElections();
}
