package authorization.repository.interfaces;

import java.util.UUID;

import authorization.domain.model.BlindSignatureModel;

public interface BlindSignatureRepositoryPort {
    public BlindSignatureModel save(BlindSignatureModel model);
    public boolean existsByUserIdAndElectionId(UUID userId, UUID electionId);
}
