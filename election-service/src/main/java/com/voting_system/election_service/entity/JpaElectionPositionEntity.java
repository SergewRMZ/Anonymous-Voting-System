package com.voting_system.election_service.entity;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
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
@Table (
    name = "elections_positions",
    uniqueConstraints = {
        @UniqueConstraint (
            name = "unique_election_position",
            columnNames = { "election_id", "position_id" }
        )
    }

)
@Getter 
@AllArgsConstructor 
@NoArgsConstructor 
@Builder 
public class JpaElectionPositionEntity {
    @Id 
    @GeneratedValue 
    private UUID id;

    @ManyToOne (optional = false)
    @JoinColumn (name = "election_id", nullable = false)
    private JpaElectionEntity election;

    @ManyToOne (optional = false)
    @JoinColumn (name = "position_id", nullable = false)
    private JpaPositionEntity position;
}
