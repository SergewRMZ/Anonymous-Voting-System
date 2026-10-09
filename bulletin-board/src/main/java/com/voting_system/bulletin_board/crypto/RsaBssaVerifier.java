package com.voting_system.bulletin_board.crypto;

import java.security.GeneralSecurityException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.PSSParameterSpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.stereotype.Component;

import com.voting_system.bulletin_board.exceptions.CryptographicOperationException;
import com.voting_system.bulletin_board.exceptions.RSAPublicKeyException;

@Component 
public class RsaBssaVerifier implements DigitalSignatureVerifierPort {
    private static final int N_MODULUS = 3072;
    private static final int SALT_LENGTH = 48;

    /**
     * Definición de que se utilizará SHA3-384 como función hash criptográfica
     * además de una MGF1 que internamente utilizará SHA3-384.
     */
    private static final PSSParameterSpec PSS_PARAMETERS =
        new PSSParameterSpec(
            "SHA3-384", 
            "MGF1", 
            new MGF1ParameterSpec("SHA3-384"), 
            SALT_LENGTH, 
            1
        );

    @Override
    public boolean verify(
        byte[] message, 
        byte[] signature, 
        PublicKey publicKey
    ) {
    
        RSAPublicKey rsaPublicKey = (RSAPublicKey) publicKey;
        if(rsaPublicKey.getModulus().bitLength() != N_MODULUS) {
            throw new RSAPublicKeyException("RSA Public Key must be 3072 bits");
        }

        try {
            Signature verifier = Signature.getInstance("RSASSA-PSS", BouncyCastleProvider.PROVIDER_NAME);
            verifier.setParameter(PSS_PARAMETERS);
            verifier.initVerify(publicKey);
            verifier.update(message);

            return verifier.verify(signature);
        } catch (GeneralSecurityException ex) {
            throw new CryptographicOperationException(
                "An internal cryptographic error has occurred", ex);
        }
    }
}