package com.voting_system.election_service.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.voting_system.election_service.entity.JpaElectionEntity;

public interface IElectionRepository extends JpaRepository<JpaElectionEntity, UUID> {}
