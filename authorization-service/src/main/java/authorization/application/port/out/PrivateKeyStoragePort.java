package authorization.application.port.out;
import java.util.UUID;

public interface PrivateKeyStoragePort {
    void savePrivateKey(UUID authorizationKeysId, byte[] privateKeyBytes);
    byte[] readPrivateKey(UUID authorizationKeysId);
}
