package com.voting_system.tally_service.exception;

/**
 * Exception que se ejecuta cuando los criptogramas que representan a un voto
 * no corresponden a puntos de la curva elíptica secp256k1 utilizada para
 * la implementación de ECCElGamal.
 * InvalidEllipticCurvePointsException
 */
public class InvalidEllipticCurvePointsException extends RuntimeException {
    public InvalidEllipticCurvePointsException() {
        super("Ciphertexts contain invalid elliptic curve points");
    }
}
