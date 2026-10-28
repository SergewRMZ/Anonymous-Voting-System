package com.voting_system.tally_service.service;

import java.math.BigInteger;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
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
import com.voting_system.tally_service.crypto.ElGamalDecryptor;
import com.voting_system.tally_service.crypto.EncryptedResult;
import com.voting_system.tally_service.exception.ElectionNotClosedException;
import com.voting_system.tally_service.model.TallyModel;
import com.voting_system.tally_service.repository.PrivateEncryptionKeyRepository;

import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class TallyService {
    private final BulletinBoardServiceClient bulletinBoardServiceClient;
    private final ElectionServiceClient electionServiceClient;
    private final ElGamalAcumulator elGamalAcumulator;
    private final ElGamalDecryptor elGamalDecryptor;
    private final PrivateEncryptionKeyRepository privateEncryptionKeyRepository;

    public List<TallyModel> tallyVotes(UUID electionId) {
        GetElectionStatusDto getElectionStatusResponse = electionServiceClient.getElectionStatus(electionId);
        if(getElectionStatusResponse.status() != ElectionStatus.CLOSED) {
            throw new ElectionNotClosedException(electionId);
        }

        GetElectionStructureDto response = electionServiceClient.getElectionStructureDto(electionId);
        List<EncryptedResult> encryptedResults = new ArrayList<>();
        
        response.positions().forEach(position -> 
            position.candidacies().forEach(candidacyId -> 
                encryptedResults.add(
                    processBallots(
                        electionId, 
                        position.electionPositionId(), 
                        candidacyId
                ))
            )
        );

        List<TallyModel> results = processEncryptedResults(electionId, encryptedResults);
        return results;
    }

    private List<TallyModel> processEncryptedResults(UUID electionId, List<EncryptedResult> encryptedResults) {
        BigInteger privateKeyScalar = new BigInteger(1, 
            privateEncryptionKeyRepository.readPrivateKey(electionId)
        );

        return encryptedResults.stream()
            .map(result -> {
                int votes = elGamalDecryptor.decrypt(
                    result.c1(), 
                    result.c2(), 
                    privateKeyScalar, 
                    result.ballotCount()
                );

                return TallyModel.builder()
                    .electionPositionId(result.electionPositionId())
                    .candidacyId(result.candidacyId())
                    .totalVotes(result.ballotCount())
                    .result(votes)
                    .createdAt(Instant.now())
                    .build();
            })
            .toList();
    }

    private EncryptedResult processBallots(UUID electionId, UUID electionPositionId, UUID candidacyId) {
        elGamalAcumulator.resetAccumulator();
        int page = 0;
        int size = 100;
        int ballotCount = 0;
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

            ballotCount += response.content().size();
            last = response.last();
            page++;
        }   while(!last);

        return new EncryptedResult(
            electionId,
            electionPositionId,
            candidacyId,
            elGamalAcumulator.getAccumulatedC1(),
            elGamalAcumulator.getAccumulatedC2(),
            ballotCount
        );
    }
}
