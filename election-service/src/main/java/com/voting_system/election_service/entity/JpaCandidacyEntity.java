package com.voting_system.election_service.entity;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity 
@Getter 
@NoArgsConstructor 
@AllArgsConstructor 
@Builder 
@Table (
    name = "candidacies",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uq_candidate_election_position",
            columnNames = {"candidate_id", "election_position_id"}
        )
    }
)

public class JpaCandidacyEntity {
    @Id 
    @GeneratedValue (strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn (name = "candidate_id", nullable = false)
    private JpaCandidateEntity candidateEntity;

    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn (name = "election_position_id", nullable = false)
    private JpaElectionPositionEntity electionPositionEntity;

    @Column (nullable = false, updatable = false)
    private Instant createdAt;
}
