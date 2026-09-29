package authorization.services;

import authorization.domain.model.BlindSignaturePublicKeyModel;
import authorization.domain.model.KeyStatus;
import authorization.dto.BlindSignatureRequest;
import authorization.repository.interfaces.BlindSignaturePublicKeyRepositoryPort;
import authorization.services.cripto.RsaBssaService;
import authorization.services.interfaces.IBlindSignatureService;
import authorization.domain.model.BlindSignatureModel;

import java.time.Instant;
import java.util.Base64;
import java.util.UUID;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import authorization.domain.exception.PublicKeyNotActiveForElectionException;
import authorization.domain.exception.SigningKeysNotLoadedException;

@Service 
@RequiredArgsConstructor 
public class BlindSignatureService implements IBlindSignatureService {
    private final BlindSignaturePublicKeyRepositoryPort blindSignaturePublicKeyRepositoryPort;
    private final RsaBssaService rsaBssaService;

    @Override
    public BlindSignatureModel generateBlindSignature(UUID electionId, BlindSignatureRequest request) {
        BlindSignaturePublicKeyModel publicKeyModel = blindSignaturePublicKeyRepositoryPort
            .findByElectionIdAndStatus(electionId, KeyStatus.ACTIVE)
            .orElseThrow(() -> new PublicKeyNotActiveForElectionException(electionId));
        

        if(!rsaBssaService.hasKeysLoaded()) {
            throw new SigningKeysNotLoadedException(electionId);
        }

        byte[] blindedMessageBytes = Base64
            .getDecoder()
            .decode(request.blindedMessage());

        byte[] blindSignature = rsaBssaService.sign(blindedMessageBytes);
        String blindSignatureB64 = Base64.getEncoder().encodeToString(blindSignature);
        
        return BlindSignatureModel.builder()
            .id(publicKeyModel.getId())
            .electionId(electionId)
            .blindSignature(blindSignatureB64)
            .issuedAt(Instant.now())
            .build();
    }
}