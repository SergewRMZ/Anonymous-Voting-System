package keys.controller;
import java.util.UUID;

import keys.services.CryptoKeysService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.nimbusds.jose.jwk.RSAKey;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
@RequestMapping("/keys")
public class CryptoKeysController {
    private final CryptoKeysService cryptoKeysService;
    public CryptoKeysController(CryptoKeysService authorizationService) {
        this.cryptoKeysService = authorizationService;
    }

    @PostMapping("/{electionId}")
    public ResponseEntity<?> generateKeys(@PathVariable UUID electionId) {
        RSAKey jwk = cryptoKeysService.generateElectionKeys(electionId);
        return ResponseEntity.status(HttpStatus.CREATED).body(jwk.toJSONObject());
    }
}
