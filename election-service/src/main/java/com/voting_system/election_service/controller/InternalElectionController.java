package com.voting_system.election_service.controller;

import java.time.Instant;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.voting_system.election_service.dto.InternalElectionStatusResponse;
import com.voting_system.election_service.config.InternalTokenValidator;
import com.voting_system.election_service.services.interfaces.IElectionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/internal/elections")
@RequiredArgsConstructor
public class InternalElectionController {
    private final IElectionService electionService;
    private final InternalTokenValidator internalTokenValidator;

    @GetMapping("/{electionId}/status")
    public ResponseEntity<InternalElectionStatusResponse> getStatus(
        @PathVariable UUID electionId,
        @RequestHeader("X-Internal-Token") String internalToken
    ) {
        internalTokenValidator.validate(internalToken);
        return ResponseEntity.ok(InternalElectionStatusResponse.from(
            electionService.getElection(electionId),
            Instant.now()
        ));
    }

}
