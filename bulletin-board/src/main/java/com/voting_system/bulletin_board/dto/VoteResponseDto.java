package com.voting_system.bulletin_board.dto;

import java.util.UUID;

public record VoteResponseDto(
    UUID voteId,
    String message
) {
    public VoteResponseDto(UUID voteId) {
        this(voteId, "Vote has been registered successfully at the Bulletin Board");
    }
}
