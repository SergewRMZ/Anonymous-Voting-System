package com.voting_system.election_service.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.junit.jupiter.api.Test;

import com.voting_system.election_service.exceptions.InvalidElectionDateException;
import com.voting_system.election_service.exceptions.InvalidElectionStateException;

class ElectionModelUpdateTest {
    private final Instant start = Instant.now().plus(1, ChronoUnit.DAYS);
    private final Instant end = start.plus(1, ChronoUnit.HOURS);

    private ElectionModel electionWithStatus(ElectionStatus status) {
        return ElectionModel.builder()
            .name("Original")
            .description("Original description")
            .startDate(start)
            .endDate(end)
            .status(status)
            .build();
    }

    @Test
    void updatesConfigurationWhenElectionIsDraft() {
        ElectionModel election = electionWithStatus(ElectionStatus.DRAFT);
        Instant newStart = start.plus(2, ChronoUnit.DAYS);
        Instant newEnd = newStart.plus(3, ChronoUnit.HOURS);

        election.updateConfiguration("Nueva", "Nueva descripción", newStart, newEnd);

        assertEquals("Nueva", election.getName());
        assertEquals("Nueva descripción", election.getDescription());
        assertEquals(newStart, election.getStartDate());
        assertEquals(newEnd, election.getEndDate());
        assertEquals(ElectionStatus.DRAFT, election.getStatus());
    }

    @Test
    void rejectsUpdateWhenElectionIsNotDraft() {
        for (ElectionStatus status : ElectionStatus.values()) {
            if (status == ElectionStatus.DRAFT) continue;

            ElectionModel election = electionWithStatus(status);

            assertThrows(InvalidElectionStateException.class,
                () -> election.updateConfiguration("Nueva", "Desc", start, end),
                "Status " + status + " must not be editable");
            assertEquals("Original", election.getName());
        }
    }

    @Test
    void rejectsUpdateWhenStartIsNotBeforeEnd() {
        ElectionModel election = electionWithStatus(ElectionStatus.DRAFT);

        assertThrows(InvalidElectionDateException.class,
            () -> election.updateConfiguration("Nueva", "Desc", end, start));
        assertThrows(InvalidElectionDateException.class,
            () -> election.updateConfiguration("Nueva", "Desc", start, start));
        assertEquals("Original", election.getName());
        assertEquals(start, election.getStartDate());
    }
}
