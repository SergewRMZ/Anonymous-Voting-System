package com.voting_system.tally_service.repository;

import java.util.List;
import java.util.UUID;

import com.voting_system.tally_service.model.ElectionResultModel;

public interface ElectionResultRepositoryPort {
    List<ElectionResultModel> saveAll(List<ElectionResultModel> resultList);
    List<ElectionResultModel> findAllByElectionId(UUID electionId);
}
