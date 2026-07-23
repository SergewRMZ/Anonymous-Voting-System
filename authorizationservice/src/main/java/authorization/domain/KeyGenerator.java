package authorization.domain;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

public interface KeyGenerator {
    public KeyPair generateKeyPair();
    public PublicKey bytesToPublicKey(byte[] bytes);
    public PrivateKey bytesToPrivateKey(byte[] bytes);
}
