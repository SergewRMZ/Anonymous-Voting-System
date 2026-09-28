package com.voting_system.election_service.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.voting_system.election_service.domain.CandidacyModel;
import com.voting_system.election_service.dto.CreateCandidacyDtoRequest;
import com.voting_system.election_service.services.CandidacyService;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController 
@RequestMapping ("/api/election-service/election-positions")
@RequiredArgsConstructor 
public class CandidacyController {
    private final CandidacyService candidacyService;

    @PostMapping("/{electionPositionId}/candidacies")
    public ResponseEntity<?> createCandidacy (
        @PathVariable UUID electionPositionId,
        @RequestBody CreateCandidacyDtoRequest request
    ) {
        List<CandidacyModel> list = candidacyService.createCandidacies(electionPositionId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(list);
    }
}
