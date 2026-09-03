package com.voting_system.authentication_service.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.voting_system.authentication_service.entities.RoleEntity;

public interface RoleRepository extends JpaRepository<RoleEntity, UUID> {}
