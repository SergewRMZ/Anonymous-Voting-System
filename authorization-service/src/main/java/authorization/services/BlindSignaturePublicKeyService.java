package authorization.services;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.Instant;
import java.util.Base64;
import java.util.UUID;

import org.springframework.stereotype.Service;

import authorization.domain.exception.CreatedKeyAlreadyExistsException;
import authorization.domain.exception.PublicKeyNotActiveForElectionException;
import authorization.domain.exception.PublicKeyNotFoundException;
import authorization.domain.model.BlindSignaturePublicKeyModel;
import authorization.domain.model.KeyStatus;
import authorization.mappers.RsaKeyMapper;
import authorization.repository.interfaces.BlindSignaturePrivateKeyRepositoryPort;
import authorization.repository.interfaces.BlindSignaturePublicKeyRepositoryPort;
import authorization.services.cripto.RsaBssaService;
import authorization.services.interfaces.IBlindSignaturePublicKeyService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BlindSignaturePublicKeyService implements IBlindSignaturePublicKeyService {
    private final BlindSignaturePublicKeyRepositoryPort blindSignaturePublicKeyRepository;
    private final BlindSignaturePrivateKeyRepositoryPort blindSignaturePrivateKeyRepository;
    private final RsaKeyMapper rsaKeyMapper;
    private final RsaBssaService rsaBssaService;

    private void loadKeys(BlindSignaturePublicKeyModel model) {
        byte[] privateKeyBytes = blindSignaturePrivateKeyRepository
            .readPrivateKey(model.getId());

        byte[] publicKeyBytes = Base64.getDecoder()
            .decode(model.getPublicKey());
        
        PrivateKey privateKey = rsaKeyMapper.bytesToRsaPrivateKey(privateKeyBytes);
        PublicKey publicKey = rsaKeyMapper.bytesToRsaPublicKey(publicKeyBytes);

        rsaBssaService.loadKeys(publicKey, privateKey);
    }

    @Override 
    public void loadActiveKeys() {
        blindSignaturePublicKeyRepository
            .findByStatus(KeyStatus.ACTIVE)
            .ifPresent(this::loadKeys);
    }
    
    @Override
    public BlindSignaturePublicKeyModel generateKeys(UUID electionId) {
        if(blindSignaturePublicKeyRepository.existsByElectionId(electionId)) {
            throw new CreatedKeyAlreadyExistsException(electionId);
        }

        KeyPair keyPair = rsaBssaService.generateKeys();
        byte[] privateKeyBytes = keyPair.getPrivate().getEncoded();
        byte[] publicKeyBytes = keyPair.getPublic().getEncoded();
        String publicKeyB64 = Base64.getEncoder().encodeToString(publicKeyBytes);


        BlindSignaturePublicKeyModel blindSignaturePublicKeyModel = BlindSignaturePublicKeyModel.builder()
            .electionId(electionId)
            .publicKey(publicKeyB64)
            .status(KeyStatus.CREATED)
            .createdAt(Instant.now())
            .build();

        BlindSignaturePublicKeyModel savedPublicKey = blindSignaturePublicKeyRepository
            .save(blindSignaturePublicKeyModel);

        blindSignaturePrivateKeyRepository.savePrivateKey(
            savedPublicKey.getId(), privateKeyBytes);

        return savedPublicKey;
    }

    @Override
    public BlindSignaturePublicKeyModel getByElectionId(UUID electionId) {
        return blindSignaturePublicKeyRepository.findByElectionIdAndStatus(electionId, KeyStatus.ACTIVE)
            .orElseThrow(() -> new PublicKeyNotActiveForElectionException(electionId));
    }

    @Override
    public BlindSignaturePublicKeyModel activateKeys(UUID electionId) {
        BlindSignaturePublicKeyModel model = 
            blindSignaturePublicKeyRepository.findByElectionId(electionId)
                .orElseThrow(() -> new PublicKeyNotFoundException("Public key hasn't found with Election ID " + electionId));
        
    
        if(model.getStatus() == KeyStatus.ACTIVE) {
            if(!rsaBssaService.hasKeysLoaded())
                loadKeys(model);

            return model;
        }
        
        loadKeys(model);
        model.activate();
        return blindSignaturePublicKeyRepository.save(model);
    }
}
