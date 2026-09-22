package com.voting_system.election_service.entity;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Entity 
@Table (name = "positions")
@Getter 
@AllArgsConstructor 
@Builder 
public class JpaPositionEntity {
    @Id 
    @GeneratedValue 
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn  (name = "election_id", nullable = false)
    private JpaElectionEntity electionEntity;

    @Column (nullable = false)
    private String positionName;
}
