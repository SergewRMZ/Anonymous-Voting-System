package com.voting_system.election_service.repository.interfaces;

import java.util.List;
import java.util.UUID;

import com.voting_system.election_service.domain.ElectionModel;
import com.voting_system.election_service.domain.ElectionStatus;
import com.voting_system.election_service.dto.GetElectionStatusDto;
import com.voting_system.election_service.dto.GetElectionStructureDto;

public interface IElectionRepository {
    public ElectionModel save(ElectionModel model);
    public List<ElectionModel> getElections();
    public ElectionModel getElection(UUID electionId);
    public List<ElectionModel> findByStatusIn(List<ElectionStatus> statuses);
    public GetElectionStatusDto findElectionStatusByElectionId(UUID electionId);
    public GetElectionStructureDto findElectionStructureByElectionId(UUID electionId);
}
