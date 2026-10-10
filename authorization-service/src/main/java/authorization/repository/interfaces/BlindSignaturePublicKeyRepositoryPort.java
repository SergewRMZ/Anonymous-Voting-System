package authorization.repository.interfaces;

import java.util.Optional;
import java.util.UUID;

import authorization.domain.model.BlindSignaturePublicKeyModel;
import authorization.domain.model.KeyStatus;

public interface BlindSignaturePublicKeyRepositoryPort {
    public BlindSignaturePublicKeyModel save(BlindSignaturePublicKeyModel blindSignaturePublicKeyModel);
    public Optional<BlindSignaturePublicKeyModel> findById(UUID authorizationKeyId);
    public Optional<BlindSignaturePublicKeyModel> findByStatus(KeyStatus status);
    public Optional<BlindSignaturePublicKeyModel> findByElectionId(UUID electionId);
    public Optional<BlindSignaturePublicKeyModel> findByElectionIdAndStatus(UUID electionId, KeyStatus status);
    public boolean existsByElectionId(UUID electionId);
}
