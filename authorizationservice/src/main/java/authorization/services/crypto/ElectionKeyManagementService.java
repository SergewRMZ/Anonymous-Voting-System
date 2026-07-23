package authorization.services.crypto;

import java.security.KeyPair;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.nimbusds.jose.jwk.RSAKey;

import authorization.domain.KeyGenerator;
import authorization.repository.KeyRepository;

@Service
public class ElectionKeyManagementService {
    private final KeyGenerator keyGenerator;
    private final KeyRepository keyRepository;

    public ElectionKeyManagementService(KeyGenerator keyGenerator, KeyRepository keyRepository) {
        this.keyGenerator = keyGenerator;
        this.keyRepository = keyRepository;
    }

    public RSAKey generateAndStoreElectionKeys(UUID electionId) {
        KeyPair keyPair = keyGenerator.generateKeyPair();
        keyRepository.saveKeyPair(electionId, keyPair);
        return JwkMapper.toJwk( (RSAPublicKey) keyPair.getPublic());
    }
}
