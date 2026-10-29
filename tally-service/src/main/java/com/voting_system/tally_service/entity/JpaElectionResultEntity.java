package com.voting_system.tally_service.entity;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity 
@Table (
    name = "election_results",
    uniqueConstraints = {
        @UniqueConstraint (
            name = "uk_tally_election_position_candidacy",
            columnNames = {"election_id", "election_position_id", "candidacy_id"}
        )
    },

    indexes = {
        @Index (
            name = "idx_election_results_by_election_id",
            columnList = "election_id"
        )
    }

)
@Getter @Setter
@AllArgsConstructor 
@NoArgsConstructor 
@Builder 
public class JpaElectionResultEntity {
    @Id 
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column (name = "election_id", nullable = false)
    private UUID electionId;

    @Column (name = "election_position_id", nullable = false)
    private UUID electionPositionId;

    @Column (name = "candidacy_id", nullable = false)
    private UUID candidacyId;

    @Column (name = "result", nullable = false)
    private int result;

    @Column (name = "created_at", nullable = false)
    private Instant createdAt;

    @PrePersist 
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = Instant.now();
        }
    }
}
