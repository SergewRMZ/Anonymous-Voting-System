package authorization.services.cripto;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.BigIntegers;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class RsaBssaService {
    private final int N_MODULUS = 3072;
    private ConcurrentHashMap<UUID, PrivateKey> authorizationKeys = new ConcurrentHashMap<>();

    public boolean hasKeysLoaded(UUID authorizationKeysId) {
        return authorizationKeys.containsKey(authorizationKeysId);
    }

    public void loadKeys(
        UUID authorizationKeysId,
        PrivateKey privateKey
    ) {
        authorizationKeys.putIfAbsent(authorizationKeysId, privateKey);
    }
    
    public KeyPair generateKeys() {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", BouncyCastleProvider.PROVIDER_NAME);
            generator.initialize(N_MODULUS);
            return generator.generateKeyPair();
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            throw new RuntimeException("Error durante la generación de claves criptográficas");
        }
    }

    public byte[] sign(UUID authorizationKeysId, byte[] blindedMessage) {
        RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) getPrivateKey(authorizationKeysId);
        BigInteger m = new BigInteger(1, blindedMessage);
        BigInteger d = rsaPrivateKey.getPrivateExponent();
        BigInteger n = rsaPrivateKey.getModulus();

        BigInteger blindSignature = m.modPow(d, n);

        int k = (n.bitLength() + 7) / 8;

        byte[] signatureBytes = BigIntegers.asUnsignedByteArray(k, blindSignature);
        return signatureBytes;
    }

    private PrivateKey getPrivateKey (UUID authorizationKeysId) {
        PrivateKey privateKey = authorizationKeys.get(authorizationKeysId);
        if(privateKey == null) {
            throw new IllegalStateException(
                "There's not a private key for authorization keys ID"
                + authorizationKeysId
            );
        }

        return privateKey;
    }
}
