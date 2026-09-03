package authorization.application.services;
import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Service;

import authorization.application.dto.GenerateKeyPair;
import authorization.application.port.in.CreateAuthorizationKeysUseCase;
import authorization.application.port.in.GetAuthorizationPublicKeyUseCase;
import authorization.application.port.out.KeyStoragePort;
import authorization.application.port.out.KeyRepositoryPort;
import authorization.application.port.out.KeyGeneratorPort;
import authorization.domain.exception.PublicKeyNotFoundException;
import authorization.domain.model.AuthorizationPublicKeyModel;
import authorization.domain.model.KeyStatus;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthorizationKeysService implements CreateAuthorizationKeysUseCase, GetAuthorizationPublicKeyUseCase {
    private final KeyRepositoryPort authorizationKeyPairRepository;
    private final KeyStoragePort keyStoragePort;
    private final KeyGeneratorPort keyGeneratorPort;

    @Override
    public AuthorizationPublicKeyModel generateKeyPair(UUID electionId) {
        GenerateKeyPair generatedKeyPair = keyGeneratorPort.generate();

        AuthorizationPublicKeyModel authorizationKeys = AuthorizationPublicKeyModel.builder()
            .electionId(electionId)
            .publicKey(generatedKeyPair.getPublicKey())
            .status(KeyStatus.ACTIVE)
            .createdAt(Instant.now())
            .build();

        AuthorizationPublicKeyModel savedKey = authorizationKeyPairRepository.save(authorizationKeys);

        keyStoragePort.savePrivateKey(savedKey.getAuthorizationKeysId(), generatedKeyPair.getPrivateKey());
        return savedKey;
    }

    @Override
    public AuthorizationPublicKeyModel getByElectionId(UUID electionId) {
        return authorizationKeyPairRepository.findByElectionId(electionId).
            orElseThrow(() -> new PublicKeyNotFoundException(electionId));
    }
}
