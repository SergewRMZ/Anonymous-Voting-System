package com.voting_system.bulletin_board.crypto;

import java.security.PublicKey;

public interface DigitalSignatureVerifierPort {
    public boolean verify(byte[] message, byte[] signature, PublicKey publicKey);
}
