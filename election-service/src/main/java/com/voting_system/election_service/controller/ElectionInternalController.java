package com.voting_system.election_service.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.voting_system.election_service.dto.GetElectionStatusDto;
import com.voting_system.election_service.dto.GetElectionStructureDto;
import com.voting_system.election_service.services.interfaces.IElectionService;

import lombok.RequiredArgsConstructor;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RestController 
@RequestMapping ("/api/election-service/elections/{electionId}")
@RequiredArgsConstructor 
public class ElectionInternalController {
    private final IElectionService electionService;

    @GetMapping("/state")
    public ResponseEntity<GetElectionStatusDto> getElectionStatus(@PathVariable UUID electionId) {
        return ResponseEntity.ok(electionService.getElectionStatus(electionId));
    }

    @GetMapping("/structure")
    public ResponseEntity<GetElectionStructureDto> getElectionStructure(@PathVariable UUID electionId) {
        return ResponseEntity.ok(electionService.getElectionStructure(electionId));
    }
}
