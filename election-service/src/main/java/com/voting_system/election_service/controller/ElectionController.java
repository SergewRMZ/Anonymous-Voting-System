package com.voting_system.election_service.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import com.voting_system.election_service.domain.ElectionModel;
import com.voting_system.election_service.dto.CreateElectionDtoRequest;
import com.voting_system.election_service.dto.ElectionDetailsDtoResponse;
import com.voting_system.election_service.dto.ElectionDtoResponse;
import com.voting_system.election_service.services.interfaces.IElectionService;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RequestMapping("/api/election-service/elections")
@RestController 
@RequiredArgsConstructor 
public class ElectionController {
    private final IElectionService electionService;

    @PostMapping("")
    public ResponseEntity<ElectionDtoResponse> createElection(@RequestBody CreateElectionDtoRequest createElectionDtoRequest) {        
        ElectionModel electionModel = electionService.createElection(createElectionDtoRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(
            ElectionDtoResponse.from(electionModel)
        );
    }   

    @GetMapping("")
    public ResponseEntity<List<ElectionModel>> getElections() {
        return ResponseEntity.ok(electionService.getElections());
    }

    @GetMapping("/{electionId}")
    public ResponseEntity<ElectionDetailsDtoResponse> getElectionDetails(@PathVariable UUID electionId) {
        return ResponseEntity.ok(electionService.getElectionDetails(electionId));
    }
}
