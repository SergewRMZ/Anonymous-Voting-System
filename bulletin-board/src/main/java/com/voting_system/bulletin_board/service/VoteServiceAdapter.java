package com.voting_system.bulletin_board.service;

import java.security.PublicKey;
import java.util.Base64;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.voting_system.bulletin_board.client.AuthorizationPublicKey;
import com.voting_system.bulletin_board.client.AuthorizationServiceClient;
import com.voting_system.bulletin_board.client.ElectionServiceClient;
import com.voting_system.bulletin_board.crypto.AuthorizationPublicKeyCache;
import com.voting_system.bulletin_board.crypto.CryptoUtils;
import com.voting_system.bulletin_board.crypto.RsaBssaVerifier;
import com.voting_system.bulletin_board.dto.VoteRequestDto;
import com.voting_system.bulletin_board.dto.VoteResponseDto;
import com.voting_system.bulletin_board.exceptions.DuplicateVoteException;
import com.voting_system.bulletin_board.exceptions.ElectionNotOpenException;
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
    private final ElectionServiceClient electionServiceClient;
    private final AuthorizationPublicKeyCache authorizationPublicKeyCache;
    private final RsaBssaVerifier rsaBssaVerifier;

    @Override 
    @Transactional
    public VoteResponseDto submitVote(UUID electionId, VoteRequestDto voteRequest) {
        ensureElectionAcceptsVotes(electionId);

        if(voteRepositoryPort.existsByElectionIdAndSignature(electionId, voteRequest.signature())) {
            throw new DuplicateVoteException();
        }
        
        VoteModel voteModel = voteRequest.toModel(electionId);
        UUID authorizationKeysId = voteModel.getVote().authorizationKeysId();
        
        // Si no existe la clave pública en la caché, la solicitará al servicio la almacenará.
        if(!authorizationPublicKeyCache.hasPublicKey(authorizationKeysId)) {
            AuthorizationPublicKey authorizationPublicKey = authorizationServiceClient.getPublicKey(electionId);
            byte[] n = CryptoUtils.base64UrlToBytes(authorizationPublicKey.publicKey().n());
            byte[] e = CryptoUtils.base64ToBytes(authorizationPublicKey.publicKey().e());

            PublicKey publicKey = rsaBssaVerifier.fromModulusAndExponent(n, e);
            authorizationPublicKeyCache.put(authorizationKeysId, publicKey);
        }
        
        ObjectMapper objectMapper = new ObjectMapper();
        String voteJson = objectMapper.writeValueAsString(voteModel.getVote());
        String voteJsonCanonicalized = JsonUtils.canonicalize(voteJson);
        
        byte[] calculatedDigest = CryptoUtils.sha3_384(voteJsonCanonicalized);
        byte[] receivedDigest = Base64.getDecoder().decode(voteModel.getDigest());

        if(!CryptoUtils.isSameDigest(receivedDigest, calculatedDigest)) {
            throw new VoteVerificationException();
        }
        
        PublicKey publicKey = authorizationPublicKeyCache.getPublicKey(authorizationKeysId);
        byte[] signature = CryptoUtils.base64ToBytes(voteModel.getSignature());
        byte[] randomizer = CryptoUtils.base64ToBytes(voteModel.getRandomizer());
        byte[] preparedMessage = CryptoUtils.concatenate(randomizer, calculatedDigest);

        if(!rsaBssaVerifier.verify(preparedMessage, signature, publicKey)) {
            throw new VoteVerificationException();
        }

        if (voteRepositoryPort.lockAdmissionAndIsClosed(electionId)) {
            throw new ElectionNotOpenException();
        }
        ensureElectionAcceptsVotes(electionId);
        if(voteRepositoryPort.existsByElectionIdAndSignature(electionId, voteRequest.signature())) {
            throw new DuplicateVoteException();
        }

        voteModel = voteRepositoryPort.save(voteModel);
        return new VoteResponseDto(voteModel.getId());
    }

    @Override
    @Transactional
    public void closeAdmission(UUID electionId) {
        voteRepositoryPort.closeAdmission(electionId);
    }

    private void ensureElectionAcceptsVotes(UUID electionId) {
        if (!electionServiceClient.getStatus(electionId).acceptingVotes()) {
            throw new ElectionNotOpenException();
        }
    }
}
