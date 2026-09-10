package authorization.application.port.out;

import java.util.Optional;
import java.util.UUID;

import authorization.domain.model.ElectionPublicKeyModel;
import authorization.domain.model.KeyStatus;

public interface PublicKeyRepositoryPort {
    public ElectionPublicKeyModel save(ElectionPublicKeyModel keyPair);
    public Optional<ElectionPublicKeyModel> findByElectionId(UUID electionId);
    public Optional<ElectionPublicKeyModel> findByElectionIdAndStatus(UUID electionId, KeyStatus status);
    public boolean existsByElectionId(UUID electionId);
}
