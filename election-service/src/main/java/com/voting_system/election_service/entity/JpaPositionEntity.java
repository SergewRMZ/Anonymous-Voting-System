package com.voting_system.election_service.entity;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity 
@Table (name = "positions")
@Getter 
@NoArgsConstructor 
@AllArgsConstructor 
@Builder 
public class JpaPositionEntity {
    @Id 
    @GeneratedValue 
    private UUID id;

    @Column (nullable = false)
    private String positionName;
}
