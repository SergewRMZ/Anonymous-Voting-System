package com.voting_system.bulletin_board.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.voting_system.bulletin_board.dto.CreateVoteRequestDto;
import com.voting_system.bulletin_board.dto.CreateVoteResponseDto;
import com.voting_system.bulletin_board.dto.VoteRecordResponseDto;
import com.voting_system.bulletin_board.service.interfaces.IVoteService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController 
@RequestMapping ("/api/bulletin-board/elections")
@RequiredArgsConstructor
public class VoteController {
    private final IVoteService voteService;

    @PostMapping("/{electionId}/votes")
    public ResponseEntity<CreateVoteResponseDto> submitVote(
        @PathVariable UUID electionId,
        @Valid @RequestBody CreateVoteRequestDto request
    ) { 
        return ResponseEntity.status(HttpStatus.CREATED).body(voteService.submitVote(electionId, request));
    }

    @GetMapping("/{electionId}/votes")
    public ResponseEntity<Page<VoteRecordResponseDto>> getVotes(
        @PathVariable UUID electionId,
        @RequestParam(required = false) UUID electionPositionId,
        @RequestParam(required = false) UUID candidacyId,
        @RequestParam (defaultValue = "0") int page,
        @RequestParam (defaultValue = "10") int size
    
    ) {

        if(page < 0 || size < 1) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Page must be non-negative value and size must be greater that one"
            );
        }

        return ResponseEntity.ok(
            voteService.getVotes(electionId, electionPositionId, candidacyId, page, size)
        );
    }
}
