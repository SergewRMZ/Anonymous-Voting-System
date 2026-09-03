package com.voting_system.authentication_service.services;

import org.springframework.stereotype.Service;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.crypto.ECDSASigner;
import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.gen.ECKeyGenerator;

import jakarta.annotation.PostConstruct;
import lombok.Getter;

@Service
@Getter
public class JwtService {
    private ECKey ecKey;
    private ECDSASigner signer;

    @PostConstruct
    public void initialize() {
        try {
            this.ecKey = new ECKeyGenerator(Curve.P_256)
                .keyUse(KeyUse.SIGNATURE)
                .keyIDFromThumbprint(true)
                .generate();

            this.signer = new ECDSASigner(this.ecKey);
        } catch (JOSEException e) {
            throw new RuntimeException("Error while generating elliptic curve keys", e);
        }
    }
}
