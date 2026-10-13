package com.voting_system.bulletin_board.repository.jpa;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Lock;

import jakarta.persistence.LockModeType;
import com.voting_system.bulletin_board.entity.JpaVoteAdmissionEntity;

public interface JpaVoteAdmissionRepository extends JpaRepository<JpaVoteAdmissionEntity, UUID> {
    @Modifying
    @Query(
        value = """
            INSERT INTO election_vote_admission (election_id, closed)
            VALUES (:electionId, false)
            ON CONFLICT (election_id) DO NOTHING
            """,
        nativeQuery = true
    )
    void ensureGateExists(@Param("electionId") UUID electionId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT gate FROM JpaVoteAdmissionEntity gate WHERE gate.electionId = :electionId")
    Optional<JpaVoteAdmissionEntity> findLockedByElectionId(@Param("electionId") UUID electionId);
}
