package com.voting_system.tally_service.client.dto;

import java.util.UUID;

public record VoteRecordResponseDto (
    UUID voteId,
    EncryptedVote encryptedVote,
    String randomizer,
    String signature
) {}
