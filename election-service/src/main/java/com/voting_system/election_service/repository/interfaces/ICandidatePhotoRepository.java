package com.voting_system.election_service.repository.interfaces;

import java.util.Optional;
import java.util.UUID;

import com.voting_system.election_service.domain.CandidatePhotoModel;

public interface ICandidatePhotoRepository {
    public void save(CandidatePhotoModel photo);
    public Optional<CandidatePhotoModel> findByCandidateId(UUID candidateId);
}
