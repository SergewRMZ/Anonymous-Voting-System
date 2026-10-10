package com.voting_system.bulletin_board.mapper;

import org.springframework.stereotype.Component;

import com.voting_system.bulletin_board.entity.JpaVoteEntity;
import com.voting_system.bulletin_board.model.VoteModel;

@Component 
public class VoteMapper {
    public VoteModel toModel(JpaVoteEntity entity) {
        return VoteModel.builder()
            .id(entity.getId())
            .electionId(entity.getElectionId())
            .vote(entity.getVote())
            .randomizer(entity.getRandomizer())
            .digest(entity.getDigest())
            .signature(entity.getSignature())
            .build();
    }

    public JpaVoteEntity toEntity(VoteModel model) {
        return JpaVoteEntity.builder()
            .id(model.getId())
            .electionId(model.getElectionId())
            .vote(model.getVote())
            .randomizer(model.getRandomizer())
            .digest(model.getDigest())
            .signature(model.getSignature())
            .build();
    }
}
