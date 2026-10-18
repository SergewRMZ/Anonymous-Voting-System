package com.voting_system.bulletin_board.repository.jpa;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.voting_system.bulletin_board.entity.JpaVoteEntity;
import com.voting_system.bulletin_board.repository.VoteBallotProjection;

public interface JpaVoteRepository extends JpaRepository<JpaVoteEntity, UUID> {
    public boolean existsByElectionIdAndSignature(UUID electionId, String signature);
    public Page<JpaVoteEntity> findAllByElectionId(UUID electionId, Pageable pageable);
    @Query(value = """
        SELECT
            v.id AS id,
            v.election_id AS electionId,
            (pos->>'electionPositionId')::uuid AS electionPositionId,
            (ballots->>'candidacyId')::uuid AS candidacyId,
            ballots->'vote'->>'c1' AS c1,
            ballots->'vote'->>'c2' AS c2
        FROM votes v,
        LATERAL jsonb_array_elements(v.vote->'positions') AS pos,
        LATERAL jsonb_array_elements(pos->'ballots') AS ballots
        WHERE v.election_id = :electionId
          AND (pos->>'electionPositionId')::uuid = :electionPositionId
          AND (ballots->>'candidacyId')::uuid = :candidacyId
        """, nativeQuery = true)
    public List<VoteBallotProjection> findBallotsByElectionPositionIdAndCandidacyId(
        @Param("electionId") UUID electionId,
        @Param("electionPositionId") UUID electionPositionId,
        @Param("candidacyId") UUID candidacyId
    );
}
