package com.voting_system.authentication_service.services;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.nimbusds.jose.jwk.ECKey;

public class JwtServiceTest {
    private JwtService jwtService;

    @BeforeEach
    public void setUp() {
        jwtService = new JwtService();
        jwtService.initialize();
    }

    @Test
    @DisplayName("Generar un par de claves criptográficas")
    public void testEcKeyFormat() {
        ECKey ecKey = jwtService.getEcKey();
        ECKey publicKey = ecKey.toPublicJWK();

        System.out.println(publicKey.toJSONString());
    }
}
