package authorization.repository.jpa;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import authorization.entity.JpaBlindSignatureEntity;

public interface JpaBlindSignatureRepositoryPort extends JpaRepository<JpaBlindSignatureEntity, UUID> {
    public boolean existsByUserIdAndElectionId(UUID userId, UUID electionId);
}
