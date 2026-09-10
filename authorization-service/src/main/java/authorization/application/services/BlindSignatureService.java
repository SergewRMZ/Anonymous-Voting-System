package authorization.application.services;

import authorization.application.port.in.GenerateBlindSignatureUseCase;
import authorization.application.port.out.PrivateKeyStoragePort;
import authorization.application.port.out.PublicKeyRepositoryPort;
import authorization.domain.model.ElectionPublicKeyModel;
import authorization.domain.model.KeyStatus;
import authorization.domain.model.AuthorizedVoterModel;

import java.time.Instant;
import java.util.Base64;
import java.util.UUID;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import authorization.application.port.out.SignerPort;
import authorization.domain.exception.PublicKeyNotActiveForElectionException;

@Service 
@RequiredArgsConstructor 
public class BlindSignatureService implements GenerateBlindSignatureUseCase {
    private final PrivateKeyStoragePort keyStoragePort;
    private final PublicKeyRepositoryPort publicKeyRepositoryPort;
    private final SignerPort signerPort;
    @Override
    public AuthorizedVoterModel generateBlindSignature(UUID electionId, String message) {
        ElectionPublicKeyModel publicKeyModel = publicKeyRepositoryPort
            .findByElectionIdAndStatus(electionId, KeyStatus.ACTIVE)
            .orElseThrow(() -> new PublicKeyNotActiveForElectionException(electionId));
        
        byte[] privateKeyBytes = keyStoragePort.readPrivateKey(publicKeyModel.getAuthorizationKeysId());
        byte[] messageBytes = Base64.getDecoder().decode(message);
        String blindSignature = signerPort.sign(privateKeyBytes, messageBytes);
        
        return AuthorizedVoterModel.builder()
            .electionId(electionId)
            .authorizationKeysId(publicKeyModel.getAuthorizationKeysId())
            .blindSignature(blindSignature)
            .issuedAt(Instant.now())
            .build();
    }
}