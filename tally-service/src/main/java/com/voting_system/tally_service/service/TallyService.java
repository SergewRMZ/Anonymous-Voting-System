package com.voting_system.tally_service.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.voting_system.tally_service.client.BulletinBoardServiceClient;
import com.voting_system.tally_service.client.ElectionServiceClient;
import com.voting_system.tally_service.client.dto.ElectionStatus;
import com.voting_system.tally_service.client.dto.GetElectionStatusDto;
import com.voting_system.tally_service.exception.ElectionNotClosedException;

import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class TallyService {
    private final BulletinBoardServiceClient bulletinBoardServiceClient;
    private final ElectionServiceClient electionServiceClient;

    public String tallyVotes(UUID electionId) {
        GetElectionStatusDto response = electionServiceClient.getElectionStatus(electionId);
        if(response.status() != ElectionStatus.CLOSED) {
            throw new ElectionNotClosedException(electionId);
        }

        return "Todo bien";
    }
}
