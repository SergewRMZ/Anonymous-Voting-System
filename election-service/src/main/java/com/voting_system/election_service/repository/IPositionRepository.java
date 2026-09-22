package com.voting_system.election_service.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.voting_system.election_service.entity.JpaPositionEntity;

public interface IPositionRepository extends JpaRepository<JpaPositionEntity, UUID> {} 
