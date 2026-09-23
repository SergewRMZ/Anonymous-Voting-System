package com.voting_system.tally_service.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.voting_system.tally_service.dto.EncryptionKeyDtoResponse;
import com.voting_system.tally_service.dto.UpdateEncryptionKeyStatusDto;
import com.voting_system.tally_service.model.EncryptionKeyModel;
import com.voting_system.tally_service.service.EncryptionKeyService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;



@RestController 
@RequestMapping ("/api/tally-service/election")
@RequiredArgsConstructor 
public class EncryptionKeyController {
    private final EncryptionKeyService encryptionKeyService;

    @PostMapping("/{electionId}/encryption-keys")
    public ResponseEntity<EncryptionKeyDtoResponse> create(@PathVariable UUID electionId) {
        EncryptionKeyModel model = encryptionKeyService.createEncryptionKeys(electionId);
        return ResponseEntity.status(HttpStatus.CREATED).body(
            EncryptionKeyDtoResponse.from(model)
        );
    }

    @PatchMapping("/{electionId}/encryption-keys/status")
    public ResponseEntity<EncryptionKeyDtoResponse> updateStatus(
        @PathVariable UUID electionId,
        @Valid @RequestBody UpdateEncryptionKeyStatusDto request
    ) {

        EncryptionKeyModel model = encryptionKeyService.setEncryptionKeyStatus(electionId, request);
        return ResponseEntity.status(HttpStatus.OK).body(
            EncryptionKeyDtoResponse.from(model)
        );
    }

    @GetMapping("/{electionId}/encryption-keys/public")
    public ResponseEntity<EncryptionKeyDtoResponse> getMethodName(@PathVariable UUID electionId) {
        EncryptionKeyModel model = encryptionKeyService.getPublicEncryptionKey(electionId);
        return ResponseEntity.status(HttpStatus.OK).body(EncryptionKeyDtoResponse.from(model));
    }
    

    // ENDPOINT DE PRUEBA, ESTE LO USARÉ PARA PROBAR DESCIFRADO
    @PostMapping("/decrypt")
    public String decryptVote(@RequestBody String message) {
        return "Decrypt Function";
    }
    
}
