package com.voting_system.election_service.entity;

import java.time.Instant;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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
@Table (name = "candidates")
@Getter 
@NoArgsConstructor
@AllArgsConstructor 
@Builder  
public class JpaCandidateEntity {
    @Id 
    @GeneratedValue 
    private UUID id;

    @Column (nullable = false, length = 50)
    private String name;

    @Column (name = "last_name", nullable = false, length = 50)
    private String lastName;

    @Column (nullable = false, length = 300)
    private String description;

    @Column (nullable = false, name = "is_active")
    private boolean isActive;
    
    @CreationTimestamp 
    @Column (nullable = false)
    private Instant createdAt;
    
    @UpdateTimestamp 
    private Instant updatedAt;
}
