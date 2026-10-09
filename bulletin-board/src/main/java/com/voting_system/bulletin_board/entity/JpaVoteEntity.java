package com.voting_system.bulletin_board.entity;

import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.voting_system.bulletin_board.model.EncryptedVote;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter 
@AllArgsConstructor 
@Builder 
@Entity 
@Table (name = "votes")
public class JpaVoteEntity {
    @Id 
    @GeneratedValue 
    private UUID id;

    @Column (name = "election_id", nullable = false)
    private UUID electionId;

    @JdbcTypeCode (SqlTypes.JSON)
    @Column (name = "vote", columnDefinition = "jsonb", nullable = false)
    private EncryptedVote vote;

    @Column (name = "randomizer", nullable = false)
    private String randomizer;

    @Column (name = "digest", nullable = false)
    private String digest;

    @Column (name = "signature", nullable = false)
    private String signature;
}