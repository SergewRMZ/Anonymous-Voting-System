package authorization.infrastructure.adapter.out;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.stereotype.Component;

import authorization.application.dto.GenerateKeyPair;
import authorization.application.port.out.KeyGeneratorPort;
import authorization.application.port.out.VoteSignerPort;

@Component
public class RsaBssaAdapter implements KeyGeneratorPort, VoteSignerPort {
    private final int N_MODULUS = 3072;
    @Override
    public GenerateKeyPair generate() {
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
    public byte[] signVote(byte[] votePayload, byte[] privateKey) {
        // Implement the signing logic here
        return new byte[0]; // Placeholder return value
    }
}
