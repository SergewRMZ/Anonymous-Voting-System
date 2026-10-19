package com.voting_system.election_service.repository.jpa;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.voting_system.election_service.domain.ElectionStatus;
import com.voting_system.election_service.dto.ElectionStructureRowDto;
import com.voting_system.election_service.dto.GetElectionStatusDto;
import com.voting_system.election_service.entity.JpaElectionEntity;

public interface JpaElectionRepository extends JpaRepository<JpaElectionEntity, UUID> {
    List<JpaElectionEntity> findAllByStatusIn(List<ElectionStatus> statuses);
    @Query ("""
            SELECT 
                new com.voting_system.election_service.dto.GetElectionStatusDto(
                    e.id, 
                    e.status
                )

                FROM JpaElectionEntity e 
                WHERE e.id = :electionId
            """)
    Optional<GetElectionStatusDto> findElectionStatusByElectionId(@Param("electionId") UUID electionId);

    @Query ("""
            SELECT 
                new com.voting_system.election_service.dto.ElectionStructureRowDto(
                    ep.id,
                    c.id
                )

                FROM JpaElectionEntity e
                JOIN e.electionPositions ep
                JOIN ep.candidacies c
                WHERE e.id = :electionId
        """)
    List<ElectionStructureRowDto> findElectionStructureByElectionId(@Param ("electionId") UUID electionId);
}
