package authorization.infrastructure.adapter.out;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataAuthorizationKeysRepository extends JpaRepository<JpaAuthorizationKeysEntity, UUID> {
    Optional<JpaAuthorizationKeysEntity> findByElectionId(UUID electionId);
}
