package com.voting_system.tally_service.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.voting_system.tally_service.client.BulletinBoardServiceClient;
import com.voting_system.tally_service.client.ElectionServiceClient;
import com.voting_system.tally_service.client.dto.ElectionStatus;
import com.voting_system.tally_service.client.dto.GetElectionStatusDto;
import com.voting_system.tally_service.client.dto.GetElectionStructureDto;
import com.voting_system.tally_service.client.dto.PageResponseDto;
import com.voting_system.tally_service.client.dto.VoteBallotDto;
import com.voting_system.tally_service.crypto.CryptoUtils;
import com.voting_system.tally_service.crypto.ElGamalAcumulator;
import com.voting_system.tally_service.exception.ElectionNotClosedException;

import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class TallyService {
    private final BulletinBoardServiceClient bulletinBoardServiceClient;
    private final ElectionServiceClient electionServiceClient;
    private final ElGamalAcumulator elGamalAcumulator;

    public String tallyVotes(UUID electionId) {
        GetElectionStatusDto getElectionStatusResponse = electionServiceClient.getElectionStatus(electionId);
        if(getElectionStatusResponse.status() != ElectionStatus.CLOSED) {
            throw new ElectionNotClosedException(electionId);
        }

        GetElectionStructureDto getElectionStructureResponse = electionServiceClient.getElectionStructureDto(electionId);
        getElectionStructureResponse.positions().forEach(position -> 
            position.candidacies().forEach(candidacyId -> 
                processBallots(
                    electionId, 
                    position.electionPositionId(), 
                    candidacyId
                )
            )
        );
        return "FINALIZADO FALTA DESCIFRAR Y PERSISTIR";
    }

    private void processBallots(UUID electionId, UUID electionPositionId, UUID candidacyId) {
        elGamalAcumulator.resetAccumulator();
        int page = 0;
        int size = 100;
        boolean last;
        do {
            PageResponseDto<VoteBallotDto> response = bulletinBoardServiceClient
                .getBallots(electionId, electionPositionId, candidacyId, page, size);
            
            response.content().stream()
                .forEach(ballot -> 
                    elGamalAcumulator.add(
                        CryptoUtils.base64ToBytes(ballot.c1()), 
                        CryptoUtils.base64ToBytes(ballot.c2())
                    )
                );

            last = response.last();
            page++;
        }   while(!last);

        String accumulatedC1 = elGamalAcumulator.getAccumulatedC1();
        String accumulatedC2 = elGamalAcumulator.getAccumulatedC2();
        System.out.println(accumulatedC1);
        System.out.println(accumulatedC2);
    }
}
