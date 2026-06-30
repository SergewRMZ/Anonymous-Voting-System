package keys.services;

import java.security.KeyPair;
import java.security.Security;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.stereotype.Service;

import com.nimbusds.jose.jwk.RSAKey;

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
        fileService.saveFileToPem(privateKeyFile, "PRIVATE KEY", keyPair.getPrivate().getEncoded());  
        return rsaPssService.publicKeyToJwk((RSAPublicKey) keyPair.getPublic());
    }
}