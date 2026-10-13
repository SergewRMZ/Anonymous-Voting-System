package com.voting_system.election_service.domain;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;

import org.junit.jupiter.api.Test;

class ElectionModelTest {
    @Test
    void voteEligibilityRequiresActiveStatusAndAnOpenDateWindow() {
        Instant start = Instant.parse("2030-01-01T10:00:00Z");
        Instant end = Instant.parse("2030-01-01T11:00:00Z");
        ElectionModel election = ElectionModel.builder()
            .startDate(start)
            .endDate(end)
            .status(ElectionStatus.ACTIVE)
            .build();

        assertFalse(election.canAcceptVotes(start.minusNanos(1)));
        assertTrue(election.canAcceptVotes(start));
        assertTrue(election.canAcceptVotes(end.minusNanos(1)));
        assertFalse(election.canAcceptVotes(end));

        election.setStatus(ElectionStatus.CLOSED);
        assertFalse(election.canAcceptVotes(start.plusSeconds(1)));
    }

    @Test
    void closingIsResumableAndDisablesVotingBeforeTheBulletinBoardFreeze() {
        ElectionModel election = ElectionModel.builder()
            .startDate(Instant.parse("2030-01-01T10:00:00Z"))
            .endDate(Instant.parse("2030-01-01T11:00:00Z"))
            .status(ElectionStatus.ACTIVE)
            .build();

        election.beginClosing();
        election.beginClosing();

        assertFalse(election.canAcceptVotes(Instant.parse("2030-01-01T10:30:00Z")));
        election.completeClosing();
        election.finishElection();
        assertTrue(election.getStatus() == ElectionStatus.FINISHED);
    }
}
