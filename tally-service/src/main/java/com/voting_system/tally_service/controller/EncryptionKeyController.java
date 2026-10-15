package com.voting_system.tally_service.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.voting_system.tally_service.dto.EncryptionKeyDtoResponse;
import com.voting_system.tally_service.model.EncryptionKeyModel;
import com.voting_system.tally_service.service.EncryptionKeyService;

import lombok.RequiredArgsConstructor;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;


@RestController 
@RequestMapping ("/api/tally-service/elections/{electionId}")
@RequiredArgsConstructor 
public class EncryptionKeyController {
    private final EncryptionKeyService encryptionKeyService;

    @PostMapping("/encryption-keys")
    public ResponseEntity<EncryptionKeyDtoResponse> create(@PathVariable UUID electionId) {
        EncryptionKeyModel model = encryptionKeyService.createEncryptionKeys(electionId);
        return ResponseEntity.status(HttpStatus.CREATED).body(
            EncryptionKeyDtoResponse.from(model)
        );
    }

    @PatchMapping("/encryption-keys/activate")
    public ResponseEntity<EncryptionKeyDtoResponse> updateStatus(
        @PathVariable UUID electionId
    ) {

        EncryptionKeyModel model = encryptionKeyService.activateEncryptionKeys(electionId);
        return ResponseEntity.status(HttpStatus.OK).body(
            EncryptionKeyDtoResponse.from(model)
        );
    }

    @GetMapping("/encryption-keys/public")
    public ResponseEntity<EncryptionKeyDtoResponse> getPublicKeyByElectionId(@PathVariable UUID electionId) {
        EncryptionKeyModel model = encryptionKeyService.getPublicEncryptionKey(electionId);
        return ResponseEntity.status(HttpStatus.OK).body(EncryptionKeyDtoResponse.from(model));
    }

    @PostMapping("/tally")
    public ResponseEntity<?> tallyVotes(@PathVariable UUID electionId) {
        
        
        return ResponseEntity.ok("Conteo de votos");
    }
    
}
