package com.voting_system.bulletin_board.repository;
import java.util.UUID;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.voting_system.bulletin_board.entity.JpaVoteEntity;
import com.voting_system.bulletin_board.mapper.VoteMapper;
import com.voting_system.bulletin_board.model.VoteModel;
import com.voting_system.bulletin_board.repository.interfaces.VoteRepositoryPort;
import com.voting_system.bulletin_board.repository.jpa.JpaVoteRepository;
import com.voting_system.bulletin_board.repository.jpa.JpaVoteAdmissionRepository;
import com.voting_system.bulletin_board.entity.JpaVoteAdmissionEntity;

import lombok.RequiredArgsConstructor;

@Repository 
@RequiredArgsConstructor 
public class VoteRepositoryAdapter implements VoteRepositoryPort {
    private final JpaVoteRepository jpaVoteRepository;
    private final JpaVoteAdmissionRepository jpaVoteAdmissionRepository;
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
    public List<com.voting_system.bulletin_board.model.EncryptedVote> findEncryptedVotesByElectionId(UUID electionId) {
        return jpaVoteRepository.findAllByElectionId(electionId).stream()
            .map(JpaVoteEntity::getVote)
            .toList();
    }

    @Override
    public boolean lockAdmissionAndIsClosed(UUID electionId) {
        jpaVoteAdmissionRepository.ensureGateExists(electionId);
        return jpaVoteAdmissionRepository.findLockedByElectionId(electionId)
            .map(JpaVoteAdmissionEntity::isClosed)
            .orElseThrow(() -> new IllegalStateException("Vote admission gate could not be created"));
    }

    @Override
    public void closeAdmission(UUID electionId) {
        jpaVoteAdmissionRepository.ensureGateExists(electionId);
        JpaVoteAdmissionEntity gate = jpaVoteAdmissionRepository.findLockedByElectionId(electionId)
            .orElseThrow(() -> new IllegalStateException("Vote admission gate could not be created"));
        gate.close();
        jpaVoteAdmissionRepository.save(gate);
    }
}
