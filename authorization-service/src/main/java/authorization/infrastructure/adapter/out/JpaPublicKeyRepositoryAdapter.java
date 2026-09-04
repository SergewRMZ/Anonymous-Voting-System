package authorization.infrastructure.adapter.out;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import authorization.application.port.out.PublicKeyRepositoryPort;
import authorization.domain.model.AuthorizationPublicKeyModel;
import authorization.domain.model.KeyStatus;
import lombok.AllArgsConstructor;

@Repository
@AllArgsConstructor
public class JpaPublicKeyRepositoryAdapter implements PublicKeyRepositoryPort {
    private final AuthorizationPublicKeyMapper authorizationKeysPersistenceMapper;
    private final SpringDataAuthorizationKeysRepository springDataAuthorizationKeysRepository;
    
    /**
     * Saves the given AuthorizationPublicKeyModel to the database and returns the saved model.
     * @param authorizationKeyPair The AuthorizationPublicKeyModel to be saved.
     */
    @Override
    public AuthorizationPublicKeyModel save(AuthorizationPublicKeyModel authorizationKeyPair) {
        JpaAuthorizationKeysEntity jpaAuthorizationKeysEntity = authorizationKeysPersistenceMapper.toEntity(authorizationKeyPair);
        JpaAuthorizationKeysEntity savedAuthorizationKeys = springDataAuthorizationKeysRepository.save(jpaAuthorizationKeysEntity);
        return authorizationKeysPersistenceMapper.toDomain(savedAuthorizationKeys);
    }

    /**
     * Finds an AuthorizationPublicKeyModel by the given electionId.
     * @param electionId The UUID of the election for which to find the AuthorizationPublicKeyModel
     */
    @Override
    public Optional<AuthorizationPublicKeyModel> findByElectionId(UUID electionId) {
        return springDataAuthorizationKeysRepository.findByElectionId(electionId)
            .map(authorizationKeysPersistenceMapper::toDomain);
    }

    @Override
    public boolean existsKeyByElectionId(UUID electionId, KeyStatus status) {
        return springDataAuthorizationKeysRepository.existsByElectionIdAndStatus(electionId, status);
    }
}
