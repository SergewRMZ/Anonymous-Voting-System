package com.voting_system.election_service.repository.jpa;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.voting_system.election_service.entity.JpaElectionPositionEntity;

public interface JpaElectionPositionRepository extends JpaRepository<JpaElectionPositionEntity, UUID> {
    @EntityGraph(attributePaths = "position")
    List<JpaElectionPositionEntity> findAllByElection_Id(UUID electionId);
}
