package com.voting_system.bulletin_board.crypto;

import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.PSSParameterSpec;
import java.security.spec.RSAPublicKeySpec;

import org.springframework.stereotype.Component;

import com.voting_system.bulletin_board.exceptions.CryptographicOperationException;
import com.voting_system.bulletin_board.exceptions.RSAPublicKeyException;

@Component 
public class RsaBssaVerifier implements DigitalSignatureVerifierPort {
    private static final int N_MODULUS = 3072;
    private static final int SALT_LENGTH = 48;

    /**
     * Método que permite obtener una clave pública de RSA a partir
     * del módulo n y el exponente e.
     * @param n Valor del módulo n
     * @param e Valor del exponente público
     * @return
     */
    public PublicKey fromModulusAndExponent(byte[] n, byte[] e) {
        BigInteger modulus = new BigInteger(1, n);
        BigInteger exponent = new BigInteger(1, e);

        RSAPublicKeySpec keySpec = new RSAPublicKeySpec(modulus, exponent);
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(keySpec);
        }   catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
            throw new CryptographicOperationException("Failed to reconstruct RSA Public Key", ex);
        }
    }

    /**
     * Definición de que se utilizará SHA3-384 como función hash criptográfica
     * además de utilizar una MGF1 que internamente utilizará SHA3-384.
     */
    private static final PSSParameterSpec PSS_PARAMETERS =
        new PSSParameterSpec(
            "SHA3-384", 
            "MGF1", 
            new MGF1ParameterSpec("SHA3-384"), 
            SALT_LENGTH, 
            1
        );

    /**    (non-Javadoc)
     * Función para realizar la verificación de la firma digital
     * @param message Define el mensaje que fue firmado. Es una cadena de 384 bits.
     * @param signature Define la firma digital que será verificada.
     * @param publicKey Define la clave pública utilizada para verificar
     * la firma digital. Debe ser una instancia de RSAPublicKey.
     * @see com.voting_system.bulletin_board.crypto.DigitalSignatureVerifierPort#verify(byte[], byte[], java.security.PublicKey)
     */
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
            Signature verifier = Signature.getInstance("RSASSA-PSS");
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