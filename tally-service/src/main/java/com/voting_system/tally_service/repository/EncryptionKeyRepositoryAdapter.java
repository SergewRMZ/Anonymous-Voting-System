package com.voting_system.tally_service.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.voting_system.tally_service.entity.JpaEncryptionKeyEntity;
import com.voting_system.tally_service.mappers.EncryptionKeyMapper;
import com.voting_system.tally_service.model.EncryptionKeyModel;

import lombok.RequiredArgsConstructor;

@Repository 
@RequiredArgsConstructor 
public class EncryptionKeyRepositoryAdapter implements IEncryptionKeyRepository {
    private final EncryptionKeyMapper encryptionKeyMapper;
    private final JpaEncryptionRepository jpaEncryptionRepository;
    @Override
    public Optional<EncryptionKeyModel> findByElectionId(UUID electionId) {
        return jpaEncryptionRepository.findByElectionId(electionId)
            .map(encryptionKeyMapper::toModel);
    }

    @Override
    public boolean existsByElectionId(UUID electionId) {
        return jpaEncryptionRepository.existsByElectionId(electionId);
    }

    @Override
    public EncryptionKeyModel save(EncryptionKeyModel model) {
        JpaEncryptionKeyEntity encryptionKeyEntity = jpaEncryptionRepository.save(encryptionKeyMapper.toEntity(model));
        return encryptionKeyMapper.toModel(encryptionKeyEntity);
    }
}
