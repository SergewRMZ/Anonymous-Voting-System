package com.voting_system.bulletin_board.repository.interfaces;

import java.util.UUID;

import com.voting_system.bulletin_board.model.VoteModel;

public interface VoteRepositoryPort {
    public VoteModel save(VoteModel vote);
    public boolean existsByElectionIdAndSignature(UUID electionId, String signature);
}
