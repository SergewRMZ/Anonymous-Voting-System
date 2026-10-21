package com.voting_system.tally_service.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.voting_system.tally_service.service.TallyService;

import lombok.RequiredArgsConstructor;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController 
@RequestMapping ("/api/tally-service/elections/{electionId}")
@RequiredArgsConstructor 

public class TallyController {
    private final TallyService tallyService;

    @PostMapping("/tally")
    public ResponseEntity<?> tallyVotes(@PathVariable UUID electionId) {
        return ResponseEntity.ok(tallyService.tallyVotes(electionId));
    }
}
