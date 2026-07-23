package authorization.services.crypto;

import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import java.security.KeyPair;
import java.security.KeyFactory;
import java.security.KeyPairGenerator;
import java.security.PublicKey;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
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

    public PublicKey bytesToPublicKey(byte[] publicKeyBytes) {
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA", BouncyCastleProvider.PROVIDER_NAME);
            return keyFactory.generatePublic(keySpec);
        } catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidKeySpecException e) {
            throw new RuntimeException("Error al convertir bytes a clave pública", e);
        }
    }

    public PrivateKey bytesToPrivateKey(byte[] privateKeyBytes) {
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA", BouncyCastleProvider.PROVIDER_NAME);
            return keyFactory.generatePrivate(keySpec);
        } catch (Exception e) {
            throw new RuntimeException("Error al mapear una clave privada");
        }
    }
}
