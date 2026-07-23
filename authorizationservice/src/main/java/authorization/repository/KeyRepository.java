package authorization.repository;

import java.security.KeyPair;
import java.util.UUID;

public interface KeyRepository {
    void saveKeyPair(UUID electionId, KeyPair keyPair);
    byte[] readPublicKey(UUID electionId);
    byte[] readPrivateKey(UUID electionId);
}
