package com.voting_system.election_service.repository.jpa;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.voting_system.election_service.domain.ElectionStatus;
import com.voting_system.election_service.entity.JpaElectionEntity;

public interface JpaElectionRepository extends JpaRepository<JpaElectionEntity, UUID> {
    List<JpaElectionEntity> findAllByStatusIn(List<ElectionStatus> statuses);
}
