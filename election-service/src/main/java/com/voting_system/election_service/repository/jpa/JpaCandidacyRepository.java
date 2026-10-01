package com.voting_system.election_service.repository.jpa;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.voting_system.election_service.entity.JpaCandidacyEntity;

public interface JpaCandidacyRepository extends JpaRepository<JpaCandidacyEntity, UUID> {
    @EntityGraph(attributePaths = "candidateEntity")
    List<JpaCandidacyEntity> findAllByElectionPositionEntity_IdIn(Collection<UUID> electionPositionIds);
}
