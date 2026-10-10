package authorization.repository;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import authorization.domain.model.BlindSignaturePublicKeyModel;
import authorization.domain.model.KeyStatus;
import authorization.entity.JpaBlindSignaturePublicKeyEntity;
import authorization.mappers.BlindSignaturePublicKeyMapper;
import authorization.repository.interfaces.BlindSignaturePublicKeyRepositoryPort;
import authorization.repository.jpa.JpaBlindSignaturePublicKeyRepository;
import lombok.AllArgsConstructor;

@Repository
@AllArgsConstructor
public class BlindSignaturePublicKeyRepositoryAdapter implements BlindSignaturePublicKeyRepositoryPort {
    private final BlindSignaturePublicKeyMapper blindSignaturePublicKeyMapper;
    private final JpaBlindSignaturePublicKeyRepository jpaBlindSignaturePublicKeyRepository;
    
    @Override
    public BlindSignaturePublicKeyModel save(BlindSignaturePublicKeyModel blindSignaturePublicKeyModel) {
        JpaBlindSignaturePublicKeyEntity entity = blindSignaturePublicKeyMapper.toEntity(blindSignaturePublicKeyModel);
        return blindSignaturePublicKeyMapper.toModel(jpaBlindSignaturePublicKeyRepository.save(entity));
    }

    @Override 
    public Optional<BlindSignaturePublicKeyModel> findById(UUID authorizationKeyId) {
        return jpaBlindSignaturePublicKeyRepository.findById(authorizationKeyId)
            .map(blindSignaturePublicKeyMapper::toModel);
    }

    @Override 
    public Optional<BlindSignaturePublicKeyModel> findByStatus(KeyStatus status) {
        return jpaBlindSignaturePublicKeyRepository.findByStatus(status)
            .map(blindSignaturePublicKeyMapper::toModel);
    }

    @Override
    public Optional<BlindSignaturePublicKeyModel> findByElectionId(UUID electionId) {
        return jpaBlindSignaturePublicKeyRepository.findByElectionId(electionId)
            .map(blindSignaturePublicKeyMapper::toModel);
    }

    @Override
    public Optional<BlindSignaturePublicKeyModel> findByElectionIdAndStatus(UUID electionId, KeyStatus status) {
        return jpaBlindSignaturePublicKeyRepository.findByElectionIdAndStatus(electionId, status)
            .map(blindSignaturePublicKeyMapper::toModel);
    }

    @Override 
    public boolean existsByElectionId(UUID electionId) {
        return jpaBlindSignaturePublicKeyRepository.existsByElectionId(electionId);
    }
}
