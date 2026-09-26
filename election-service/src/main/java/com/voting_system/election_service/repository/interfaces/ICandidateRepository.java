package com.voting_system.election_service.repository.interfaces;

import java.util.List;

import com.voting_system.election_service.domain.CandidateModel;

public interface ICandidateRepository {
    public CandidateModel save(CandidateModel model);
    public List<CandidateModel> getCandidates();
}
