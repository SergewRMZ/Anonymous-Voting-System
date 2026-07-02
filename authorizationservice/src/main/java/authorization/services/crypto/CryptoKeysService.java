package authorization.services.crypto;

import java.security.KeyPair;
import java.security.KeyFactory;
import java.security.Security;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.UUID;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.stereotype.Service;

import com.nimbusds.jose.jwk.RSAKey;

import authorization.services.FileService;

@Service
public class CryptoKeysService {
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
        String privateKeyFile = electionId + "_private.pem";
        String publicKeyFile = electionId + "_public.pem";
        fileService.saveFileToPem(privateKeyFile, "PRIVATE KEY", keyPair.getPrivate().getEncoded());  
        fileService.saveFileToPem(publicKeyFile, "PUBLIC KEY", keyPair.getPublic().getEncoded());
        return rsaPssService.publicKeyToJwk((RSAPublicKey) keyPair.getPublic());
    }

    public RSAKey readPublicKey(UUID electionId) {
        String publicKeyFile = electionId + "_public.pem";
        byte[] publicKeyBytes = fileService.readFileFromPem(publicKeyFile);
        RSAPublicKey publicKey = (RSAPublicKey) rsaPssService.bytesToPublicKey(publicKeyBytes);
        return rsaPssService.publicKeyToJwk(publicKey);
    }
}