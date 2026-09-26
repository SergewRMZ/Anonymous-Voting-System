package com.voting_system.election_service.entity;

import com.voting_system.election_service.domain.ElectionStatus;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "elections")
@Getter 
@AllArgsConstructor 
@NoArgsConstructor 
@Builder 
public class JpaElectionEntity {
    @Id
    @GeneratedValue
    private UUID id;

    @Column (nullable = false)
    private String name;

    @Column (nullable = false)
    private String description;

    @Enumerated (EnumType.STRING)
    private ElectionStatus status;

    @Column (nullable = false)
    private Instant startsAt;

    @Column (nullable = false)
    private Instant endsAt;
}