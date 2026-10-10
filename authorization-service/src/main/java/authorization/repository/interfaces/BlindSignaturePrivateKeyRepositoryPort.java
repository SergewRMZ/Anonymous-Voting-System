package authorization.repository.interfaces;
import java.util.UUID;

public interface BlindSignaturePrivateKeyRepositoryPort {
    void savePrivateKey(UUID authorizationKeysId, byte[] privateKeyBytes);
    byte[] readPrivateKey(UUID authorizationKeysId);
}
