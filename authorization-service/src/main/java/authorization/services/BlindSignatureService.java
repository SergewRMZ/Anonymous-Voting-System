package authorization.services;

import authorization.domain.model.BlindSignaturePublicKeyModel;
import authorization.dto.BlindSignatureRequest;
import authorization.repository.interfaces.BlindSignatureRepositoryPort;
import authorization.services.cripto.RsaBssaService;
import authorization.services.interfaces.IBlindSignaturePublicKeyService;
import authorization.services.interfaces.IBlindSignatureService;
import authorization.domain.model.BlindSignatureModel;

import java.time.Instant;
import java.util.Base64;
import java.util.UUID;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import authorization.domain.exception.BlindSignatureAlreadyExistsException;

@Service 
@RequiredArgsConstructor 
public class BlindSignatureService implements IBlindSignatureService {
    private final BlindSignatureRepositoryPort blindSignatureRepositoryPort;
    private final IBlindSignaturePublicKeyService blindSignaturePublicKeyService;
    private final RsaBssaService rsaBssaService;


    @Override
    public BlindSignatureModel generateBlindSignature(UUID electionId, UUID userId, BlindSignatureRequest request) {
        BlindSignaturePublicKeyModel publicKeyModel = blindSignaturePublicKeyService
            .getActivePublicKeyByElectionId(electionId);
        
        if(this.blindSignatureRepositoryPort.existsByUserIdAndElectionId(userId, electionId)) {
            throw new BlindSignatureAlreadyExistsException(
                userId, 
                electionId
            ); 
        } 

        if(!rsaBssaService.hasKeysLoaded(publicKeyModel.getId())) {
            blindSignaturePublicKeyService
                .loadPrivateKeyByAuthorizationKeysId(publicKeyModel.getId());
        }

        byte[] blindedMessageBytes = Base64
            .getDecoder()
            .decode(request.blindedMessage());

        byte[] blindSignature = rsaBssaService
            .sign(publicKeyModel.getId(), blindedMessageBytes);
       
        String blindSignatureB64 = Base64
            .getEncoder()
            .encodeToString(blindSignature);
        
        BlindSignatureModel model = BlindSignatureModel.builder()
            .electionId(electionId)
            .userId(userId)
            .authorizationKeysId(publicKeyModel.getId())
            .blindSignature(blindSignatureB64)
            .createdAt(Instant.now())
            .build();
        // return model;
        return blindSignatureRepositoryPort.save(model);
    }
}