package com.voting_system.tally_service.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.voting_system.tally_service.client.BulletinBoardServiceClient;

import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class TallyService {
    private final BulletinBoardServiceClient bulletinBoardServiceClient;

    public void tallyVotes(UUID electionId) {
        int page = 0;
        int size = 2;
    }
}
