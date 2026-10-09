package com.voting_system.bulletin_board.service.interfaces;
import java.util.UUID;

import com.voting_system.bulletin_board.model.VoteModel;
public interface IVoteService {
    public VoteModel submitVote(UUID electionId, VoteModel voteModel);
}
