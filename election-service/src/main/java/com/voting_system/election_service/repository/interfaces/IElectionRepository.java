package com.voting_system.election_service.repository.interfaces;

import java.util.List;

import com.voting_system.election_service.domain.ElectionModel;

public interface IElectionRepository {
    public ElectionModel save(ElectionModel model);
    public List<ElectionModel> getElections();
}
