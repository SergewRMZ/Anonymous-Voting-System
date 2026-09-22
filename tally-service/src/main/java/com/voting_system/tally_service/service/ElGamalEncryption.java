package com.voting_system.tally_service.service;

import java.security.SecureRandom;

import org.bouncycastle.asn1.sec.SECNamedCurves;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.generators.ECKeyPairGenerator;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECKeyGenerationParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.springframework.stereotype.Service;


@Service 
public class ElGamalEncryption {
    private final X9ECParameters params;
    private AsymmetricCipherKeyPair keyPair;
    public ElGamalEncryption() {
        this.params = SECNamedCurves.getByName("secp256k1");
    }

    public void generateKeys() {
        ECDomainParameters domainParameters = new ECDomainParameters(params);
        ECKeyGenerationParameters keyGenerationParameters = new ECKeyGenerationParameters(domainParameters, new SecureRandom());

        ECKeyPairGenerator ecKeyPairGenerator = new ECKeyPairGenerator();
        ecKeyPairGenerator.init(keyGenerationParameters);
        this.keyPair = ecKeyPairGenerator.generateKeyPair();
    }

    /**
     * This method returns a public key compressed. For secp256k1 the public key length
     * is 33 bytes.
     */
    public byte[] getPublicKey() {
        ECPublicKeyParameters publicKey = (ECPublicKeyParameters) this.keyPair.getPublic();
        return publicKey.getQ().getEncoded(true);
    }

    /**
     * This method returns the private key bytes
     * @return private key bytes of a key pair ElGamal encryption
     */
    public byte[] getPrivateKey() {
        ECPrivateKeyParameters privateKey = (ECPrivateKeyParameters) this.keyPair.getPrivate();
        return privateKey.getD().toByteArray();
    }
}
