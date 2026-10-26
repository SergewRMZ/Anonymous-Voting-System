package com.voting_system.election_service.services;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.voting_system.election_service.services.interfaces.IElectionService;

import lombok.RequiredArgsConstructor;

@Component 
@RequiredArgsConstructor 
public class ElectionStatusScheduler {
    private final IElectionService electionService;

    @Scheduled (fixedDelayString = "${election.scheduler.close-check-interval-ms}")
    public void closeExpiredElections() {
        this.electionService.closeExpiredElections();
    }
}
