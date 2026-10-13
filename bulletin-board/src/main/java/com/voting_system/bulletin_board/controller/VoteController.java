package com.voting_system.bulletin_board.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.voting_system.bulletin_board.dto.VoteRequestDto;
import com.voting_system.bulletin_board.dto.VoteResponseDto;
import com.voting_system.bulletin_board.service.interfaces.IVoteService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController 
@RequestMapping ("/api/bulletin-board/elections")
@RequiredArgsConstructor
public class VoteController {

    private final IVoteService voteService;

    @PostMapping("/{electionId}/votes")
    public ResponseEntity<VoteResponseDto> submitVote(
        @PathVariable UUID electionId,
        @Valid @RequestBody VoteRequestDto request
    ) { 
        return ResponseEntity.status(HttpStatus.CREATED).body(voteService.submitVote(electionId, request));
    }
}
