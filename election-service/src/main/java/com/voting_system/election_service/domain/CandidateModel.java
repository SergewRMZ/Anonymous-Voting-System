package com.voting_system.election_service.domain;

import java.time.Instant;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter 
@AllArgsConstructor 
@NoArgsConstructor 
@Builder 
public class CandidateModel {
    private UUID id;
    private String name;
    private String lastName;
    private String description;
    private boolean isActive;
    private Instant createdAt;
    private Instant updatedAt;
}