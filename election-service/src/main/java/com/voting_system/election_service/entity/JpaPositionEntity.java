package com.voting_system.election_service.entity;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity 
@Table (
    name = "positions",
    uniqueConstraints = {
        @UniqueConstraint (
            name = "unique_position_name",
            columnNames = { "position_name" }
        )
    }
)
@Getter 
@NoArgsConstructor 
@AllArgsConstructor 
@Builder 
public class JpaPositionEntity {
    @Id 
    @GeneratedValue 
    private UUID id;

    // Único: un cargo se reutiliza entre elecciones, no se crea dos veces.
    @Column (name = "position_name", nullable = false, length = 100)
    private String positionName;
}
