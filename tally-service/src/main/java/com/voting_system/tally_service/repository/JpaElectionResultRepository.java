package com.voting_system.tally_service.repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.voting_system.tally_service.entity.JpaElectionResultEntity;

public interface JpaElectionResultRepository extends JpaRepository<JpaElectionResultEntity, UUID> {
    Optional<List<JpaElectionResultEntity>> findAllByElectionId(UUID electionId);
}
