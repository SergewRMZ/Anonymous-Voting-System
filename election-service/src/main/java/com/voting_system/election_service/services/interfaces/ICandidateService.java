package com.voting_system.election_service.services.interfaces;

import com.voting_system.election_service.domain.CandidateModel;
import com.voting_system.election_service.dto.CreateCandidateDtoRequest;

public interface ICandidateService {
    public CandidateModel createCandidate(CreateCandidateDtoRequest request);
}
