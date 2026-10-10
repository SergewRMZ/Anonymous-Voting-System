package com.voting_system.bulletin_board.service.interfaces;
import java.util.UUID;

import com.voting_system.bulletin_board.dto.VoteRequest;
import com.voting_system.bulletin_board.model.VoteModel;
public interface IVoteService {
    public VoteModel submitVote(UUID electionId, VoteRequest voteRequest);
}
