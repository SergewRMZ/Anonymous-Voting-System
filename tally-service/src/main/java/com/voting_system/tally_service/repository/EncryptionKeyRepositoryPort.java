package com.voting_system.tally_service.repository;

import java.util.Optional;
import java.util.UUID;

import com.voting_system.tally_service.model.EncryptionKeyModel;

public interface EncryptionKeyRepositoryPort {
    Optional<EncryptionKeyModel> findByElectionId(UUID electionId);
    boolean existsByElectionId(UUID electionId);
    EncryptionKeyModel save(EncryptionKeyModel model);
}
