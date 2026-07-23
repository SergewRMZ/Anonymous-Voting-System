package authorization.controller;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.nimbusds.jose.jwk.RSAKey;

import authorization.services.crypto.CryptoKeysService;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/keys")
public class CryptoKeyController {
    private final CryptoKeysService cryptoKeysService;
    public CryptoKeyController(CryptoKeysService authorizationService) {
        this.cryptoKeysService = authorizationService;
    }

    @PostMapping("/{electionId}")
    public ResponseEntity<?> generateKeys(@PathVariable UUID electionId) {
        RSAKey jwk = cryptoKeysService.generateElectionKeys(electionId);
        return ResponseEntity.status(HttpStatus.CREATED).body(jwk.toJSONObject());
    }

    @GetMapping("/{electionId}")
    public ResponseEntity<?> getPublicKey(@PathVariable UUID electionId) {
        RSAKey jwk = cryptoKeysService.readPublicKeyFromPem(electionId);
        return ResponseEntity.ok(jwk.toJSONObject());
    }
    
}
