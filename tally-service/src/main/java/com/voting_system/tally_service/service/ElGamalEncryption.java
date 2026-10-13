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

    public ElGamalEncryption() {
        this.params = SECNamedCurves.getByName("secp256k1");
    }

    public KeyMaterial generateKeys() {
        ECDomainParameters domainParameters = new ECDomainParameters(params);
        ECKeyGenerationParameters keyGenerationParameters = new ECKeyGenerationParameters(domainParameters, new SecureRandom());

        ECKeyPairGenerator ecKeyPairGenerator = new ECKeyPairGenerator();
        ecKeyPairGenerator.init(keyGenerationParameters);
        AsymmetricCipherKeyPair keyPair = ecKeyPairGenerator.generateKeyPair();
        ECPublicKeyParameters publicKey = (ECPublicKeyParameters) keyPair.getPublic();
        ECPrivateKeyParameters privateKey = (ECPrivateKeyParameters) keyPair.getPrivate();
        return new KeyMaterial(publicKey.getQ().getEncoded(true), privateKey.getD().toByteArray());
    }

    public record KeyMaterial(byte[] publicKey, byte[] privateKey) {}
}
