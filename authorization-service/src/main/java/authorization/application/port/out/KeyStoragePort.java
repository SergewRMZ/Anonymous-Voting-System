package authorization.application.port.out;
import java.util.UUID;

public interface KeyStoragePort {
    void savePrivateKey(UUID authorizationKeysId, byte[] privateKeyBytes);
    byte[] readPrivateKey(UUID authorizationKeysId);
}
