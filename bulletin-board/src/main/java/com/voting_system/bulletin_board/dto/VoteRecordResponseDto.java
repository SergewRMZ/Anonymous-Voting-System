package com.voting_system.bulletin_board.dto;

import java.util.UUID;
import com.voting_system.bulletin_board.model.EncryptedVote;
import com.voting_system.bulletin_board.model.VoteModel;
public record VoteRecordResponseDto (
    UUID voteId,
    EncryptedVote encryptedVote,
    String randomizer, 
    String signature
) {
    public static VoteRecordResponseDto from(VoteModel voteModel) {
        return new VoteRecordResponseDto(
            voteModel.getId(),
            voteModel.getEncryptedVote(), 
            voteModel.getRandomizer(),
            voteModel.getSignature()
        );
    }
}