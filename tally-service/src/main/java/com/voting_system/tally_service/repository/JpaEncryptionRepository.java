package com.voting_system.tally_service.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.voting_system.tally_service.entity.JpaEncryptionKeyEntity;


public interface JpaEncryptionRepository extends JpaRepository<JpaEncryptionKeyEntity, UUID> {
    public Optional<JpaEncryptionKeyEntity> findByElectionId(UUID electionId);
    public boolean existsByElectionId(UUID electionId);
}
