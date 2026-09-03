package authorization.application.port.out;
import authorization.application.dto.GenerateKeyPair;

public interface KeyGeneratorPort {
    public GenerateKeyPair generate();
}
