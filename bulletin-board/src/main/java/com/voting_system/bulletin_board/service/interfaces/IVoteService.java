package com.voting_system.bulletin_board.service.interfaces;
import java.util.UUID;

import com.voting_system.bulletin_board.dto.VoteRequestDto;
import com.voting_system.bulletin_board.dto.VoteResponseDto;
public interface IVoteService {
    public VoteResponseDto submitVote(UUID electionId, VoteRequestDto voteRequest);
}
