package com.voting_system.bulletin_board.repository.interfaces;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.voting_system.bulletin_board.model.VoteModel;

public interface VoteRepositoryPort {
    public VoteModel save(VoteModel vote);
    public boolean existsByElectionIdAndSignature(UUID electionId, String signature);
    public Page<VoteModel> findAllByElectionIdAndCandidacy(
        UUID electionId,
        UUID electionPositionId,
        UUID candidacyId,
        Pageable pageable
    );
}
