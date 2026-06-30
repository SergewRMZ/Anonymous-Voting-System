package keys.services;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.interfaces.RSAPublicKey;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.stereotype.Service;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;

@Service
public class RsaPssService {
    private static final int N_MODULUS = 3072;

    public KeyPair generateKeys() {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", BouncyCastleProvider.PROVIDER_NAME);
            generator.initialize(N_MODULUS);
            return generator.generateKeyPair();
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            throw new RuntimeException("Error durante la generación de claves criptográficas");
        }
    }

    public RSAKey publicKeyToJwk(RSAPublicKey pk) {
        try {
            return new RSAKey.Builder(pk)
                .keyUse(KeyUse.SIGNATURE)
                .keyIDFromThumbprint()
                .build();
        } catch (JOSEException e) {
            throw new RuntimeException("Error al mapear la clave pública", e);
        }
    }
}
