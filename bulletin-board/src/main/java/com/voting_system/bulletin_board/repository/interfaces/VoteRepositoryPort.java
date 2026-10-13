package com.voting_system.bulletin_board.repository.interfaces;

import java.util.UUID;
import java.util.List;
import com.voting_system.bulletin_board.model.EncryptedVote;

import com.voting_system.bulletin_board.model.VoteModel;

public interface VoteRepositoryPort {
    public VoteModel save(VoteModel vote);
    public boolean existsByElectionIdAndSignature(UUID electionId, String signature);
    public List<EncryptedVote> findEncryptedVotesByElectionId(UUID electionId);
    public boolean lockAdmissionAndIsClosed(UUID electionId);
    public void closeAdmission(UUID electionId);
}
