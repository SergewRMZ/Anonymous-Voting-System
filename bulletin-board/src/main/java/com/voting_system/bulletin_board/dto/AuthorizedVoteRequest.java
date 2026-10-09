package com.voting_system.bulletin_board.dto;

import java.util.UUID;

import com.voting_system.bulletin_board.model.EncryptedVote;
import com.voting_system.bulletin_board.model.VoteModel;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
public record AuthorizedVoteRequest(
    @NotNull 
    EncryptedVote encryptedVote,
    
    @NotBlank (message = "Randomizer is an argument necesary to validate the digital signature")
    String randomizer,

    @NotBlank (message = "Digest is an argument necesary to validate the digital dignature")
    String digest,
    
    @NotBlank (message = "Digital signature is necesary")
    String signature
) {
    public VoteModel toModel(UUID electionId) {
        return VoteModel.builder()
            .electionId(electionId)
            .vote(encryptedVote)
            .randomizer(randomizer)
            .digest(digest)
            .signature(signature)
            .build();
    }
}

