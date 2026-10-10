package com.voting_system.bulletin_board.repository;

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
}
