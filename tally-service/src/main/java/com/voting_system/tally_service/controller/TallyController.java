package com.voting_system.tally_service.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.voting_system.tally_service.model.ElectionResultModel;
import com.voting_system.tally_service.service.TallyService;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;



@RestController 
@RequestMapping ("/api/tally-service/elections/{electionId}")
@RequiredArgsConstructor 

public class TallyController {
    private final TallyService tallyService;

    @PostMapping("/tally")
    public ResponseEntity<List<ElectionResultModel>> voteTallying(@PathVariable UUID electionId) {
        return ResponseEntity.ok(tallyService.voteTallying(electionId));
    }

    @GetMapping("/results")
    public ResponseEntity<List<ElectionResultModel>> getResults(@PathVariable UUID electionId) {
        return null;
    }
}
