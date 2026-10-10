package authorization.mappers;

import java.util.Base64;

import org.springframework.stereotype.Component;

import authorization.domain.model.BlindSignaturePublicKeyModel;
import authorization.dto.BlindSignaturePublicKeyResponse;
import lombok.RequiredArgsConstructor;

@Component 
@RequiredArgsConstructor 
public class BlindSignaturePublicKeyResponseMapper {
    private final JwkMapper jwkMapper;

    public BlindSignaturePublicKeyResponse toResponse(BlindSignaturePublicKeyModel model) {
        byte[] publicKeyBytes = Base64
            .getDecoder()
            .decode(model.getPublicKey());

        return new BlindSignaturePublicKeyResponse(
            model.getId(), 
            model.getElectionId(), 
            jwkMapper.bytesToJwk(publicKeyBytes),
            model.getStatus(), 
            model.getCreatedAt(), 
            model.getActivatedAt(), 
            model.getExpiredAt());
    }
}
