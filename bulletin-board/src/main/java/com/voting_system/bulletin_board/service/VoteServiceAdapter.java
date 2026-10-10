package com.voting_system.bulletin_board.service;

import java.util.Base64;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.voting_system.bulletin_board.client.AuthorizationPublicKey;
import com.voting_system.bulletin_board.client.AuthorizationServiceClient;
import com.voting_system.bulletin_board.crypto.CryptoUtils;
import com.voting_system.bulletin_board.dto.VoteRequest;
import com.voting_system.bulletin_board.exceptions.VoteVerificationException;
import com.voting_system.bulletin_board.model.VoteModel;
import com.voting_system.bulletin_board.repository.interfaces.VoteRepositoryPort;
import com.voting_system.bulletin_board.service.interfaces.IVoteService;
import com.voting_system.bulletin_board.utils.JsonUtils;

import lombok.RequiredArgsConstructor;
import tools.jackson.databind.ObjectMapper;

@Service 
@RequiredArgsConstructor 
public class VoteServiceAdapter implements IVoteService {
    private final VoteRepositoryPort voteRepositoryPort;
    private final AuthorizationServiceClient authorizationServiceClient;

    @Override 
    public VoteModel submitVote(UUID electionId, VoteRequest voteRequest) {
        VoteModel voteModel = voteRequest.toModel(electionId);
        AuthorizationPublicKey authorizationPublicKey = authorizationServiceClient.getPublicKey(electionId);
        
        ObjectMapper objectMapper = new ObjectMapper();
        String voteJson = objectMapper.writeValueAsString(voteModel.getVote());
        String voteJsonCanonicalized = JsonUtils.canonicalize(voteJson);
        
        byte[] calculatedDigest = CryptoUtils.sha3_384(voteJsonCanonicalized);
        byte[] receivedDigest = Base64.getDecoder().decode(voteModel.getDigest());

        if(!CryptoUtils.isSameDigest(receivedDigest, calculatedDigest)) {
            throw new VoteVerificationException();
        }
        
        return voteModel;
        // return voteRepositoryPort.save(voteModel);
    }
}
