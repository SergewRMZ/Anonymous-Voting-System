package com.voting_system.bulletin_board.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.voting_system.bulletin_board.model.EncryptedVote;
import com.voting_system.bulletin_board.repository.interfaces.VoteRepositoryPort;
import com.voting_system.bulletin_board.config.InternalTokenValidator;
import com.voting_system.bulletin_board.service.interfaces.IVoteService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/internal/elections")
@RequiredArgsConstructor
public class InternalVoteController {
    private final VoteRepositoryPort voteRepositoryPort;
    private final IVoteService voteService;
    private final InternalTokenValidator internalTokenValidator;

    @GetMapping("/{electionId}/votes")
    public ResponseEntity<List<EncryptedVote>> getEncryptedVotes(
        @PathVariable UUID electionId,
        @RequestHeader("X-Internal-Token") String internalToken
    ) {
        internalTokenValidator.validate(internalToken);
        return ResponseEntity.ok(voteRepositoryPort.findEncryptedVotesByElectionId(electionId));
    }

    @PostMapping("/{electionId}/close")
    public ResponseEntity<Void> closeAdmission(
        @PathVariable UUID electionId,
        @RequestHeader("X-Internal-Token") String internalToken
    ) {
        internalTokenValidator.validate(internalToken);
        voteService.closeAdmission(electionId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
