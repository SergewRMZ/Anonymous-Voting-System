package authorization.application.services;
import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Service;

import authorization.application.dto.GenerateKeyPair;
import authorization.application.port.in.CreateAuthorizationKeysUseCase;
import authorization.application.port.in.GetAuthorizationPublicKeyUseCase;
import authorization.application.port.out.PrivateKeyStoragePort;
import authorization.application.port.out.PublicKeyRepositoryPort;
import authorization.application.port.out.KeyGeneratorPort;
import authorization.domain.exception.ActiveKeyAlreadyExistsException;
import authorization.domain.exception.PublicKeyNotFoundException;
import authorization.domain.model.AuthorizationPublicKeyModel;
import authorization.domain.model.KeyStatus;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthorizationKeysService implements CreateAuthorizationKeysUseCase, GetAuthorizationPublicKeyUseCase {
    private final PublicKeyRepositoryPort publicKeyRepository;
    private final PrivateKeyStoragePort keyStoragePort;
    private final KeyGeneratorPort keyGeneratorPort;

    @Override
    public AuthorizationPublicKeyModel generateKeyPair(UUID electionId) {
        if(publicKeyRepository.existsKeyByElectionId(electionId, KeyStatus.ACTIVE)) {
            throw new ActiveKeyAlreadyExistsException(electionId);
        }

        GenerateKeyPair generatedKeyPair = keyGeneratorPort.generate();

        AuthorizationPublicKeyModel authorizationKeys = AuthorizationPublicKeyModel.builder()
            .electionId(electionId)
            .publicKey(generatedKeyPair.getPublicKey())
            .status(KeyStatus.CREATED)
            .createdAt(Instant.now())
            .build();

        AuthorizationPublicKeyModel savedKey = publicKeyRepository.save(authorizationKeys);

        keyStoragePort.savePrivateKey(savedKey.getAuthorizationKeysId(), generatedKeyPair.getPrivateKey());
        return savedKey;
    }

    @Override
    public AuthorizationPublicKeyModel getByElectionId(UUID electionId) {
        return publicKeyRepository.findByElectionId(electionId).
            orElseThrow(() -> new PublicKeyNotFoundException(electionId));
    }
}
