package com.voting_system.tally_service.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.voting_system.tally_service.dto.EncryptionKeyDtoResponse;
import com.voting_system.tally_service.dto.TallyResultResponse;
import com.voting_system.tally_service.dto.UpdateEncryptionKeyStatusDto;
import com.voting_system.tally_service.model.EncryptionKeyModel;
import com.voting_system.tally_service.service.EncryptionKeyService;
import com.voting_system.tally_service.service.TallyService;

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
@RequestMapping ("/api/tally-service/elections")
@RequiredArgsConstructor 
public class EncryptionKeyController {
    private final EncryptionKeyService encryptionKeyService;
    private final TallyService tallyService;

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
    @PostMapping("/{electionId}/tally")
    public ResponseEntity<TallyResultResponse> tally(@PathVariable UUID electionId) {
        return ResponseEntity.ok(tallyService.tally(electionId));
    }
}
