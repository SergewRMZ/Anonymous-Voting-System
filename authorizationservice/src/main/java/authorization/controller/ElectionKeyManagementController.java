package authorization.controller;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.nimbusds.jose.jwk.RSAKey;

import authorization.services.crypto.CryptoKeysService;
import authorization.services.crypto.ElectionKeyManagementService;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/keys")
public class ElectionKeyManagementController {
    private final ElectionKeyManagementService electionKeyManagementService;
    public ElectionKeyManagementController(ElectionKeyManagementService electionKeyManagementService) {
        this.electionKeyManagementService = electionKeyManagementService;
    }

    @PostMapping("/{electionId}")
    public ResponseEntity<?> generateKeys(@PathVariable UUID electionId) {
        RSAKey jwk = electionKeyManagementService.generateAndStoreElectionKeys(electionId);
        return ResponseEntity.status(HttpStatus.CREATED).body(jwk.toJSONObject());
    }

    // @GetMapping("/{electionId}")
    // public ResponseEntity<?> getPublicKey(@PathVariable UUID electionId) {
    //     RSAKey jwk = electionKeyManagementService.readPublicKeyFromPem(electionId);
    //     return ResponseEntity.ok(jwk.toJSONObject());
    // }
    
}
