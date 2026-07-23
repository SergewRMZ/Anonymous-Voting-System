package authorization.services.crypto;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import authorization.domain.KeyGenerator;

public class RsaBssaKeyGenerator implements KeyGenerator {
    private final int N_MODULUS = 3072;
    @Override
    public KeyPair generateKeyPair() {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", BouncyCastleProvider.PROVIDER_NAME);
            generator.initialize(N_MODULUS);
            return generator.generateKeyPair();
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            throw new RuntimeException("Error durante la generación de claves criptográficas");
        }
    }

    @Override
    public PublicKey bytesToPublicKey(byte[] publicKeyBytes) {
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA", BouncyCastleProvider.PROVIDER_NAME);
            return keyFactory.generatePublic(keySpec);
        } catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidKeySpecException e) {
            throw new RuntimeException("Error al construir una clave pública a partir de bytes");
        }
    }

    @Override
    public PrivateKey bytesToPrivateKey(byte[] privateKeyBytes) {
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA", BouncyCastleProvider.PROVIDER_NAME);
            return keyFactory.generatePrivate(keySpec);
        } catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidKeySpecException e) {
            throw new RuntimeException("Error al construir una clave pública a partir de bytes");
        }
    }
}
