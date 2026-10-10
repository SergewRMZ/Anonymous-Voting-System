package authorization.services.cripto;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.BigIntegers;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class RsaBssaService {
    private final int N_MODULUS = 3072;
    private KeyPair keyPair;

    public boolean hasKeysLoaded() {
        if(this.keyPair != null) return true;
        return false;
    }

    public synchronized void loadKeys(PublicKey publicKey, PrivateKey privateKey) {
        if(this.keyPair != null) {
            return;
        }
        this.keyPair = new KeyPair(publicKey, privateKey);
    }
    
    public KeyPair generateKeys() {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", BouncyCastleProvider.PROVIDER_NAME);
            generator.initialize(N_MODULUS);
            this.keyPair = generator.generateKeyPair();
            return this.keyPair;
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            throw new RuntimeException("Error durante la generación de claves criptográficas");
        }
    }

    public byte[] getPublicKey() {
        return this.keyPair.getPublic().getEncoded();
    }

    public byte[] sign(byte[] blindedMessage) {
        RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) this.keyPair.getPrivate();
        BigInteger m = new BigInteger(1, blindedMessage);
        BigInteger d = rsaPrivateKey.getPrivateExponent();
        BigInteger n = rsaPrivateKey.getModulus();

        BigInteger blindSignature = m.modPow(d, n);

        int k = (n.bitLength() + 7) / 8;

        byte[] signatureBytes = BigIntegers.asUnsignedByteArray(k, blindSignature);
        return signatureBytes;
    }
}
