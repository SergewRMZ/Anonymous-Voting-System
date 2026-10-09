package com.voting_system.bulletin_board.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.voting_system.bulletin_board.model.VoteModel;
import com.voting_system.bulletin_board.repository.interfaces.VoteRepositoryPort;
import com.voting_system.bulletin_board.service.interfaces.IVoteService;

import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class VoteServiceAdapter implements IVoteService {
    private final VoteRepositoryPort voteRepositoryPort;

    @Override 
    public VoteModel submitVote(UUID electionId, VoteModel model) {
        return voteRepositoryPort.save(model);
    }
}
