package com.voting_system.bulletin_board.repository.interfaces;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.voting_system.bulletin_board.model.VoteModel;
import com.voting_system.bulletin_board.repository.VoteBallotProjection;

public interface VoteRepositoryPort {
    public VoteModel save(VoteModel vote);
    public boolean existsByElectionIdAndSignature(UUID electionId, String signature);
    public Page<VoteModel> findAllByElectionId(UUID electionId, Pageable pageable);
    public List<VoteBallotProjection> findBallotsByElectionPositionIdAndCandidacyId(UUID electionId, UUID electionPositionId, UUID candidacyId);
}
