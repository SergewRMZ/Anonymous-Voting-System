package com.voting_system.election_service.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.voting_system.election_service.domain.CandidateModel;
import com.voting_system.election_service.dto.CandidateDtoResponse;
import com.voting_system.election_service.dto.CreateCandidateDtoRequest;
import com.voting_system.election_service.services.CandidateService;

import lombok.RequiredArgsConstructor;

import org.apache.catalina.connector.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController 
@RequestMapping ("/api/election-service/candidates")
@RequiredArgsConstructor 
public class CandidateController {
    private final CandidateService candidateService;
    @PostMapping("")
    public ResponseEntity<CandidateDtoResponse> createCandidate(@RequestBody CreateCandidateDtoRequest request) {
        CandidateModel model = candidateService.createCandidate(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(CandidateDtoResponse.fromModel(model));
    }
    
}
