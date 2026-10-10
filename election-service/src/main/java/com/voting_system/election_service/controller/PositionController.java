package com.voting_system.election_service.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.voting_system.election_service.domain.PositionModel;
import com.voting_system.election_service.dto.CreatePositionDtoRequest;
import com.voting_system.election_service.dto.PositionDtoResponse;
import com.voting_system.election_service.services.interfaces.IPositionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;

@RequestMapping ("/api/election-service/positions")
@RestController 
@RequiredArgsConstructor 
public class PositionController {
    private final IPositionService positionService;
    @PostMapping("")
    public ResponseEntity<PositionDtoResponse> createPosition(
        @Valid @RequestBody CreatePositionDtoRequest request
    ) {
        PositionModel position = positionService.createPosition(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
            PositionDtoResponse.from(position)
        );
    }

    @GetMapping("")
    public ResponseEntity<List<PositionModel>> getPositions() {
        return ResponseEntity.ok(positionService.getPositions());
    }
}
