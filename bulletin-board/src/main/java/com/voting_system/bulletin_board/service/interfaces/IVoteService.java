package com.voting_system.bulletin_board.service.interfaces;
import java.util.UUID;

import org.springframework.data.domain.Page;

import com.voting_system.bulletin_board.dto.CreateVoteRequestDto;
import com.voting_system.bulletin_board.dto.CreateVoteResponseDto;
import com.voting_system.bulletin_board.dto.VoteRecordResponseDto;
public interface IVoteService {
    public CreateVoteResponseDto submitVote(UUID electionId, CreateVoteRequestDto voteRequest);
    public Page<VoteRecordResponseDto> getVotes(UUID electionId, int page, int size);
}
