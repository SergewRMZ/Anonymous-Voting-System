package authorization.repository.jpa;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import authorization.domain.model.KeyStatus;
import authorization.entity.JpaBlindSignaturePublicKeyEntity;
import java.util.List;


public interface JpaBlindSignaturePublicKeyRepository extends JpaRepository<JpaBlindSignaturePublicKeyEntity, UUID> {
    Optional<JpaBlindSignaturePublicKeyEntity> findByElectionId(UUID electionId);
    Optional<JpaBlindSignaturePublicKeyEntity> findByStatus(KeyStatus status);
    Optional<JpaBlindSignaturePublicKeyEntity> findByElectionIdAndStatus(UUID electionId, KeyStatus status);
    boolean existsByElectionId(UUID electionId);
}
