package authorization.application.services;
import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Service;

import authorization.application.dto.GenerateKeyPair;
import authorization.application.port.in.ActiveKeyUseCase;
import authorization.application.port.in.CreateAuthorizationKeysUseCase;
import authorization.application.port.in.GetAuthorizationPublicKeyUseCase;
import authorization.application.port.out.PrivateKeyStoragePort;
import authorization.application.port.out.PublicKeyRepositoryPort;
import authorization.application.port.out.KeyGeneratorPort;
import authorization.domain.exception.CreatedKeyAlreadyExistsException;
import authorization.domain.exception.PublicKeyNotFoundException;
import authorization.domain.model.ElectionPublicKeyModel;
import authorization.domain.model.KeyStatus;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthorizationKeysService implements CreateAuthorizationKeysUseCase, GetAuthorizationPublicKeyUseCase, ActiveKeyUseCase {
    private final PublicKeyRepositoryPort publicKeyRepository;
    private final PrivateKeyStoragePort keyStoragePort;
    private final KeyGeneratorPort keyGeneratorPort;

    @Override
    public ElectionPublicKeyModel generateKeyPair(UUID electionId) {
        if(publicKeyRepository.existsByElectionId(electionId)) {
            throw new CreatedKeyAlreadyExistsException(electionId);
        }

        GenerateKeyPair generatedKeyPair = keyGeneratorPort.generateKeys();

        ElectionPublicKeyModel authorizationKeys = ElectionPublicKeyModel.builder()
            .electionId(electionId)
            .publicKey(generatedKeyPair.getPublicKey())
            .status(KeyStatus.CREATED)
            .createdAt(Instant.now())
            .build();

        ElectionPublicKeyModel savedKey = publicKeyRepository.save(authorizationKeys);

        keyStoragePort.savePrivateKey(savedKey.getAuthorizationKeysId(), generatedKeyPair.getPrivateKey());
        return savedKey;
    }

    @Override
    public ElectionPublicKeyModel getByElectionId(UUID electionId) {
        return publicKeyRepository.findByElectionId(electionId).
            orElseThrow(() -> new PublicKeyNotFoundException(electionId));
    }

    // En un próximo microservicio, se debe validar el período de tiempo.
    @Override
    public ElectionPublicKeyModel activateKey(UUID electionId) {
        ElectionPublicKeyModel publicKeyModel = publicKeyRepository.findByElectionId(electionId)
            .orElseThrow(() -> new PublicKeyNotFoundException(electionId));

        publicKeyModel.activate(Instant.now());
        return publicKeyRepository.save(publicKeyModel);
    }
}
