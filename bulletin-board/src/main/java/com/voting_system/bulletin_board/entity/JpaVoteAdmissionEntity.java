package com.voting_system.bulletin_board.entity;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "election_vote_admission")
@Getter
@NoArgsConstructor
public class JpaVoteAdmissionEntity {
    @Id
    @Column(name = "election_id", nullable = false)
    private UUID electionId;

    @Column(name = "closed", nullable = false)
    private boolean closed;

    public JpaVoteAdmissionEntity(UUID electionId) {
        this.electionId = electionId;
        this.closed = false;
    }

    public void close() {
        this.closed = true;
    }
}
