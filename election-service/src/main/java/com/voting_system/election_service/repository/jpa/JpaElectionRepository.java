package com.voting_system.election_service.repository.jpa;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.voting_system.election_service.entity.JpaElectionEntity;

public interface JpaElectionRepository extends JpaRepository<JpaElectionEntity, UUID> {
    
}
