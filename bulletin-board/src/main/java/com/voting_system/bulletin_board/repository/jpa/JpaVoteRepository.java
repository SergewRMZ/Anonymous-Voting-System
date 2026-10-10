package com.voting_system.bulletin_board.repository.jpa;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.voting_system.bulletin_board.entity.JpaVoteEntity;

public interface JpaVoteRepository extends JpaRepository<JpaVoteEntity, UUID> {}
