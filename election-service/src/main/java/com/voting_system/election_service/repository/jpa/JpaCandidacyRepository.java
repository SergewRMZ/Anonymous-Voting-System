package com.voting_system.election_service.repository.jpa;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.voting_system.election_service.entity.JpaCandidacyEntity;

public interface JpaCandidacyRepository extends JpaRepository<JpaCandidacyEntity, UUID> {}
