package com.voting_system.election_service.services.interfaces;

import java.util.List;

import com.voting_system.election_service.domain.CandidateModel;
import com.voting_system.election_service.dto.CreateCandidateDtoRequest;

public interface ICandidateService {
    public CandidateModel createCandidate(CreateCandidateDtoRequest request);
    public List<CandidateModel> getCandidates();
}
