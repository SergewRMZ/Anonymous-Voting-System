package com.voting_system.election_service.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.voting_system.election_service.domain.ElectionPositionModel;
import com.voting_system.election_service.services.ElectionPositionService;

import lombok.RequiredArgsConstructor;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;


@RequestMapping ("/api/election-service/elections")
@RestController 
@RequiredArgsConstructor 
public class ElectionPositionController {
    private final ElectionPositionService electionPositionService;

    @PostMapping("/{electionId}/positions/{positionId}")
    public ResponseEntity<?> associateElectoralPosition(@PathVariable UUID electionId, @PathVariable  UUID positionId) {
        ElectionPositionModel model = electionPositionService.associateElectoralPosition(electionId, positionId);
        return ResponseEntity.status(HttpStatus.CREATED).body(model);
    }
}
