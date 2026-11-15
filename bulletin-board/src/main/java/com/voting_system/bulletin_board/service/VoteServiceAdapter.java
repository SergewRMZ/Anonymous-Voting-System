package com.voting_system.bulletin_board.service;

import java.security.PublicKey;
import java.util.Base64;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.voting_system.bulletin_board.client.AuthorizationPublicKey;
import com.voting_system.bulletin_board.client.AuthorizationServiceClient;
import com.voting_system.bulletin_board.client.ElectionServiceClient;
import com.voting_system.bulletin_board.client.ElectionStatus;
import com.voting_system.bulletin_board.crypto.AuthorizationPublicKeyCache;
import com.voting_system.bulletin_board.crypto.CryptoUtils;
import com.voting_system.bulletin_board.crypto.RsaBssaVerifier;
import com.voting_system.bulletin_board.dto.CreateVoteRequestDto;
import com.voting_system.bulletin_board.dto.CreateVoteResponseDto;
import com.voting_system.bulletin_board.dto.VoteRecordResponseDto;
import com.voting_system.bulletin_board.exceptions.DuplicateVoteException;
import com.voting_system.bulletin_board.exceptions.ElectionNotActiveException;
import com.voting_system.bulletin_board.exceptions.UnknownAuthorizationKeyException;
import com.voting_system.bulletin_board.exceptions.VoteVerificationException;
import com.voting_system.bulletin_board.model.VoteModel;
import com.voting_system.bulletin_board.repository.VoteBallotProjection;
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
    public CreateVoteResponseDto submitVote(UUID electionId, CreateVoteRequestDto voteRequest) {
        if(voteRepositoryPort.existsByElectionIdAndSignature(electionId, voteRequest.signature())) {
            throw new DuplicateVoteException();
        }

        // Solo se reciben votos mientras la elección está activa.
        if(electionServiceClient.getElectionStatus(electionId).status() != ElectionStatus.ACTIVE) {
            throw new ElectionNotActiveException();
        }
        
        VoteModel voteModel = voteRequest.toModel(electionId);

        // La clave con la que se verifica la decide authorization-service, no el cliente:
        // el voto debe declarar la clave de autorización de esta misma elección.
        AuthorizationPublicKeyCache.ElectionKey electionKey = getElectionKey(electionId);
        if(!electionKey.authorizationKeysId().equals(voteModel.getEncryptedVote().authorizationKeysId())) {
            throw new UnknownAuthorizationKeyException();
        }

        ObjectMapper objectMapper = new ObjectMapper();
        String voteJson = objectMapper.writeValueAsString(voteModel.getEncryptedVote());
        String voteJsonCanonicalized = JsonUtils.canonicalize(voteJson);
        
        byte[] calculatedDigest = CryptoUtils.sha3_384(voteJsonCanonicalized);
        byte[] receivedDigest = Base64.getDecoder().decode(voteModel.getDigest());

        if(!CryptoUtils.isSameDigest(receivedDigest, calculatedDigest)) {
            throw new VoteVerificationException();
        }
        
        PublicKey publicKey = electionKey.publicKey();
        byte[] signature = CryptoUtils.base64ToBytes(voteModel.getSignature());
        byte[] randomizer = CryptoUtils.base64ToBytes(voteModel.getRandomizer());
        byte[] preparedMessage = CryptoUtils.concatenate(randomizer, calculatedDigest);

        if(!rsaBssaVerifier.verify(preparedMessage, signature, publicKey)) {
            throw new VoteVerificationException();
        }
        
        // Este código me sirve para meter votos y realizar pruebas
        // byte[] firmaRandom = new byte[384];
        // new SecureRandom().nextBytes(firmaRandom);
        // voteModel.setSignature(Base64.getEncoder().encodeToString(firmaRandom));

        voteModel = voteRepositoryPort.save(voteModel);
        return new CreateVoteResponseDto(voteModel.getId());
    }

    /** Clave de autorización activa de la elección, desde la caché o desde authorization-service. */
    private AuthorizationPublicKeyCache.ElectionKey getElectionKey(UUID electionId) {
        return authorizationPublicKeyCache.get(electionId).orElseGet(() -> {
            AuthorizationPublicKey authorizationPublicKey = authorizationServiceClient.getPublicKey(electionId);
            if(!electionId.equals(authorizationPublicKey.electionId())) {
                throw new VoteVerificationException();
            }

            byte[] n = CryptoUtils.base64UrlToBytes(authorizationPublicKey.publicKey().n());
            byte[] e = CryptoUtils.base64ToBytes(authorizationPublicKey.publicKey().e());
            PublicKey publicKey = rsaBssaVerifier.fromModulusAndExponent(n, e);

            authorizationPublicKeyCache.put(electionId, authorizationPublicKey.authorizationKeysId(), publicKey);
            return new AuthorizationPublicKeyCache.ElectionKey(authorizationPublicKey.authorizationKeysId(), publicKey);
        });
    }

    @Override
    public Page<VoteRecordResponseDto> getVotes(UUID electionId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return voteRepositoryPort.findAllByElectionId(electionId, pageable)
            .map(VoteRecordResponseDto::from);
    }

    public Page<VoteBallotProjection> getBallotsByPositionAndCandidacy(
        UUID electionId,
        UUID electionPositionId,
        UUID candidacyId,
        int page,
        int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return voteRepositoryPort
            .findBallotsByElectionPositionIdAndCandidacyId(
                electionId, electionPositionId, candidacyId, pageable);
    }
}
