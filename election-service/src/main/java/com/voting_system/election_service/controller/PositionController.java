package com.voting_system.election_service.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.voting_system.election_service.domain.PositionModel;
import com.voting_system.election_service.dto.CreatePositionDtoRequest;
import com.voting_system.election_service.dto.PositionDtoResponse;
import com.voting_system.election_service.services.IPositionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RequestMapping ("/api/election")
@RestController 
@RequiredArgsConstructor 
public class PositionController {
    private final IPositionService positionService;
    @PostMapping("/{electionId}/position")
    public ResponseEntity<PositionDtoResponse> createPosition(
        @PathVariable UUID electionId,
        @Valid @RequestBody CreatePositionDtoRequest request
    ) {
        PositionModel position = positionService.createPosition(request, electionId);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(
            PositionDtoResponse.from(position)
        );
    }
    
}
