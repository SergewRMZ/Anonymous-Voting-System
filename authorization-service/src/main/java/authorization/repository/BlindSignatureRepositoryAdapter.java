package authorization.repository;

import java.util.UUID;

import org.springframework.stereotype.Repository;

import authorization.domain.model.BlindSignatureModel;
import authorization.entity.JpaBlindSignatureEntity;
import authorization.entity.JpaBlindSignaturePublicKeyEntity;
import authorization.mappers.BlindSignatureMapper;
import authorization.repository.interfaces.BlindSignatureRepositoryPort;
import authorization.repository.jpa.JpaBlindSignaturePublicKeyRepository;
import authorization.repository.jpa.JpaBlindSignatureRepositoryPort;
import lombok.RequiredArgsConstructor;

@Repository 
@RequiredArgsConstructor 
public class BlindSignatureRepositoryAdapter implements BlindSignatureRepositoryPort {
    private final JpaBlindSignaturePublicKeyRepository jpaBlindSignaturePublicKeyRepository;
    private final JpaBlindSignatureRepositoryPort jpaBlindSignatureRepositoryPort;
    private final BlindSignatureMapper mapper;

    @Override 
    public BlindSignatureModel save(BlindSignatureModel model) {
        JpaBlindSignaturePublicKeyEntity publicKeyEntity = jpaBlindSignaturePublicKeyRepository
            .getReferenceById(model.getAuthorizationKeysId());
                
        JpaBlindSignatureEntity blindSignatureEntity = 
            mapper.toEntity(model, publicKeyEntity);

        blindSignatureEntity = 
            jpaBlindSignatureRepositoryPort.save(blindSignatureEntity);

        return mapper.toModel(blindSignatureEntity);
    }   

    @Override 
    public boolean existsByUserIdAndElectionId(UUID userId, UUID electionId) {
        return this.jpaBlindSignatureRepositoryPort
            .existsByUserIdAndElectionId(userId, electionId);
    }
}
