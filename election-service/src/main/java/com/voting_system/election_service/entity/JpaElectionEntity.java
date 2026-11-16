package com.voting_system.election_service.entity;

import com.voting_system.election_service.domain.ElectionStatus;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CheckConstraint;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "elections",
    // El dominio ya lo valida; la base lo garantiza aunque se escriba por otra vía.
    check = @CheckConstraint(name = "ck_elections_starts_before_ends", constraint = "starts_at < ends_at")
)
@Getter @Setter 
@AllArgsConstructor 
@NoArgsConstructor 
@Builder 
public class JpaElectionEntity {
    @Id
    @GeneratedValue
    private UUID id;

    // Mismos límites que CreateElectionDtoRequest, UpdateElectionDtoRequest y el formulario del frontend.
    @Column (nullable = false, length = 150)
    private String name;

    @Column (nullable = false, length = 1000)
    private String description;

    @Enumerated (EnumType.STRING)
    @Column (nullable = false)
    private ElectionStatus status;

    @Column (nullable = false)
    private Instant startsAt;

    @Column (nullable = false)
    private Instant endsAt;

    @OneToMany (
        mappedBy = "election", 
        cascade = CascadeType.ALL, 
        orphanRemoval = true, 
        fetch = FetchType.LAZY
    )
    private List<JpaElectionPositionEntity> electionPositions;
}