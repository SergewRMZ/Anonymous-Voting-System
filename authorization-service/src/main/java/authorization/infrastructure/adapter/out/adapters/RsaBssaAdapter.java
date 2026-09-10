package authorization.infrastructure.adapter.out.adapters;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.interfaces.RSAPrivateKey;
import java.util.Base64;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.BigIntegers;
import org.springframework.stereotype.Component;

import authorization.application.dto.GenerateKeyPair;
import authorization.application.port.out.KeyGeneratorPort;
import authorization.application.port.out.SignerPort;
import authorization.infrastructure.adapter.out.mappers.KeyMapper;

@Component
public class RsaBssaAdapter implements KeyGeneratorPort, SignerPort {
    private final int N_MODULUS = 3072;
    @Override
    public GenerateKeyPair generateKeys() {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", BouncyCastleProvider.PROVIDER_NAME);
            generator.initialize(N_MODULUS);
            KeyPair kp = generator.generateKeyPair();
            return new GenerateKeyPair(kp.getPrivate().getEncoded(), kp.getPublic().getEncoded());
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            throw new RuntimeException("Error durante la generación de claves criptográficas");
        }
    }

    @Override 
    public String sign(byte[] privateKeyBytes, byte[] message) {
        RSAPrivateKey privateKey = (RSAPrivateKey) KeyMapper.bytesToPrivateKey(privateKeyBytes);
        BigInteger m = new BigInteger(1, message);
        BigInteger d = privateKey.getPrivateExponent();
        BigInteger n = privateKey.getModulus();

        BigInteger blindSignature = m.modPow(d, n);

        int k = (n.bitLength() + 7) / 8;

        byte[] signatureBytes = BigIntegers.asUnsignedByteArray(k, blindSignature);
        return Base64.getEncoder().encodeToString(signatureBytes);
    }
}
