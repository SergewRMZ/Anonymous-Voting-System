package authorization.services;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.time.Instant;
import java.util.Base64;
import java.util.UUID;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Override 
    public void loadPrivateKeyByAuthorizationKeysId(UUID authorizationKeysId) {
        byte[] privateKeyBytes = blindSignaturePrivateKeyRepository
            .readPrivateKey(authorizationKeysId);

        PrivateKey privateKey = rsaKeyMapper.bytesToRsaPrivateKey(privateKeyBytes);
        rsaBssaService.loadKeys(authorizationKeysId, privateKey);
    }
    
    /**
     * Registra la clave pública y luego guarda la privada, en una sola transacción:
     * si falla el archivo de la clave privada, el registro se revierte y no queda una
     * clave pública sin su privada.
     */
    @Override
    @Transactional
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

        // Dos solicitudes simultáneas pueden pasar la revisión anterior: la restricción única
        // de la base rechaza la segunda, que responde igual que si la clave ya existiera.
        BlindSignaturePublicKeyModel savedPublicKey;
        try {
            savedPublicKey = blindSignaturePublicKeyRepository.save(blindSignaturePublicKeyModel);
        } catch (DataIntegrityViolationException ex) {
            throw new CreatedKeyAlreadyExistsException(electionId);
        }

        blindSignaturePrivateKeyRepository.savePrivateKey(
            savedPublicKey.getId(), privateKeyBytes);

        return savedPublicKey;
    }

    @Override
    public BlindSignaturePublicKeyModel getActivePublicKeyByElectionId(UUID electionId) {
        return blindSignaturePublicKeyRepository.findByElectionIdAndStatus(electionId, KeyStatus.ACTIVE)
            .orElseThrow(() -> new PublicKeyNotActiveForElectionException(electionId));
    }

    @Override
    public BlindSignaturePublicKeyModel getPublicKeyByElectionId(UUID electionId) {
        return blindSignaturePublicKeyRepository.findByElectionId(electionId)
            .orElseThrow(() -> new PublicKeyNotFoundException("Public key hasn't found with Election ID " + electionId));
    }

    @Override
    public BlindSignaturePublicKeyModel activateElectionKeys(UUID electionId) {
        BlindSignaturePublicKeyModel model = 
            blindSignaturePublicKeyRepository.findByElectionId(electionId)
                .orElseThrow(() -> new PublicKeyNotFoundException("Public key hasn't found with Election ID " + electionId));
            
        model.activate();
        return blindSignaturePublicKeyRepository.save(model);
    }

    @Override 
    public BlindSignaturePublicKeyModel expireActiveElectionKeys(UUID electionId) {
        BlindSignaturePublicKeyModel model = 
            blindSignaturePublicKeyRepository.findByElectionIdAndStatus(electionId, KeyStatus.ACTIVE)
                .orElseThrow(() -> new PublicKeyNotActiveForElectionException(electionId));

        model.expire();
        return blindSignaturePublicKeyRepository.save(model);
    }
}
