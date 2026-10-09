package com.voting_system.bulletin_board.repository.interfaces;

import com.voting_system.bulletin_board.model.VoteModel;

public interface VoteRepositoryPort {
    public VoteModel save(VoteModel vote);
}
