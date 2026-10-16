package com.voting_system.bulletin_board.repository.jpa;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.voting_system.bulletin_board.entity.JpaVoteEntity;

public interface JpaVoteRepository extends JpaRepository<JpaVoteEntity, UUID> {
    public boolean existsByElectionIdAndSignature(UUID electionId, String signature);

    @Query(
        value = """
            SELECT v.*
            FROM votes v
            WHERE v.election_id = :electionId
              AND (
                (
                  CAST(:electionPositionId AS text) IS NULL
                  AND CAST(:candidacyId AS text) IS NULL
                )
                OR EXISTS (
                  SELECT 1
                  FROM jsonb_array_elements(v.vote->'positions') AS pos(position_data)
                  WHERE (
                    CAST(:electionPositionId AS text) IS NULL
                    OR pos.position_data->>'electionPositionId' = CAST(:electionPositionId AS text)
                  )
                    AND (
                      CAST(:candidacyId AS text) IS NULL
                      OR EXISTS (
                        SELECT 1
                        FROM jsonb_array_elements(pos.position_data->'ballots') AS b(ballot_data)
                        WHERE b.ballot_data->>'candidacyId' = CAST(:candidacyId AS text)
                      )
                    )
                )
              )
            """,
        countQuery = """
            SELECT COUNT(*)
            FROM votes v
            WHERE v.election_id = :electionId
              AND (
                (
                  CAST(:electionPositionId AS text) IS NULL
                  AND CAST(:candidacyId AS text) IS NULL
                )
                OR EXISTS (
                  SELECT 1
                  FROM jsonb_array_elements(v.vote->'positions') AS pos(position_data)
                  WHERE (
                    CAST(:electionPositionId AS text) IS NULL
                    OR pos.position_data->>'electionPositionId' = CAST(:electionPositionId AS text)
                  )
                    AND (
                      CAST(:candidacyId AS text) IS NULL
                      OR EXISTS (
                        SELECT 1
                        FROM jsonb_array_elements(pos.position_data->'ballots') AS b(ballot_data)
                        WHERE b.ballot_data->>'candidacyId' = CAST(:candidacyId AS text)
                      )
                    )
                )
              )
            """,
        nativeQuery = true
    )
    public Page<JpaVoteEntity> findAllByElectionIdAndCandidacy(
        @Param("electionId") UUID electionId,
        @Param("electionPositionId") UUID electionPositionId,
        @Param("candidacyId") UUID candidacyId,
        Pageable pageable
    );
}
