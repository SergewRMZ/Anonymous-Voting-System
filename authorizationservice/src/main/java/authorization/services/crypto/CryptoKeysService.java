package authorization.services.crypto;

import java.security.KeyPair;
import java.security.Security;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.stereotype.Service;

import com.nimbusds.jose.jwk.RSAKey;

import authorization.services.FileService;

@Service
public class CryptoKeysService {
    private final String PRIVATE_KEY = "_private.pem";
    private final String PUBLIC_KEY = "_public.pem";

    static {
        Security.addProvider(new BouncyCastleProvider());
    }
    private final RsaPssService rsaPssService;
    private final FileService fileService;

    public CryptoKeysService(
        RsaPssService blindSignatureService,
        FileService fileService
        
    ) {
        this.rsaPssService = blindSignatureService;
        this.fileService = fileService;
    }

    public RSAKey generateElectionKeys(UUID electionId) {
        KeyPair keyPair = rsaPssService.generateKeys();
        String privateKeyFile = electionId + PRIVATE_KEY;
        String publicKeyFile = electionId + PUBLIC_KEY;
        fileService.saveFileToPem(privateKeyFile, "PRIVATE KEY", keyPair.getPrivate().getEncoded());  
        fileService.saveFileToPem(publicKeyFile, "PUBLIC KEY", keyPair.getPublic().getEncoded());
        return rsaPssService.publicKeyToJwk((RSAPublicKey) keyPair.getPublic());
    }

    public RSAKey readPublicKeyFromPem(UUID electionId) {
        String publicKeyFile = electionId + PUBLIC_KEY;
        byte[] publicKeyBytes = fileService.readFileFromPem(publicKeyFile);
        RSAPublicKey publicKey = (RSAPublicKey) rsaPssService.bytesToPublicKey(publicKeyBytes);
        return rsaPssService.publicKeyToJwk(publicKey);
    }

    public void readPrivateKeyFromPem(UUID electionId) {
        String privateKeyFile = electionId + PRIVATE_KEY;
        byte [] privateKeyBytes = fileService.readFileFromPem(privateKeyFile);
        RSAPrivateCrtKey privateKey = (RSAPrivateCrtKey) rsaPssService.bytesToPrivateKey(privateKeyBytes);
    }
}