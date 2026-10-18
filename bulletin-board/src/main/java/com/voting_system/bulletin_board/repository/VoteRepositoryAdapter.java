package com.voting_system.bulletin_board.repository;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.voting_system.bulletin_board.entity.JpaVoteEntity;
import com.voting_system.bulletin_board.mapper.VoteMapper;
import com.voting_system.bulletin_board.model.VoteModel;
import com.voting_system.bulletin_board.repository.interfaces.VoteRepositoryPort;
import com.voting_system.bulletin_board.repository.jpa.JpaVoteRepository;

import lombok.RequiredArgsConstructor;

@Repository 
@RequiredArgsConstructor 
public class VoteRepositoryAdapter implements VoteRepositoryPort {
    private final JpaVoteRepository jpaVoteRepository;
    private final VoteMapper voteMapper;

    @Override 
    public VoteModel save(VoteModel vote) {
        JpaVoteEntity voteEntity = jpaVoteRepository.save(voteMapper.toEntity(vote));
        return voteMapper.toModel(voteEntity);
    }

    @Override 
    public boolean existsByElectionIdAndSignature(UUID electionId, String signature) {
        return jpaVoteRepository.existsByElectionIdAndSignature(electionId, signature);
    }

    @Override 
    public Page<VoteModel> findAllByElectionId(UUID electionId, Pageable pageable) {
        return jpaVoteRepository.findAllByElectionId(electionId, pageable)
            .map(voteMapper::toModel);
    }

    @Override
    public List<VoteBallotProjection> findBallotsByElectionPositionIdAndCandidacyId(UUID electionId,
            UUID electionPositionId, UUID candidacyId) {

        return jpaVoteRepository
            .findBallotsByElectionPositionIdAndCandidacyId(
                electionId, 
                electionPositionId, 
                candidacyId
            );
    }
}
