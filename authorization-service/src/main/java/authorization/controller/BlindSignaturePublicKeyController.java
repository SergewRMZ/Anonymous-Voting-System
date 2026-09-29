package authorization.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;

import authorization.domain.model.BlindSignaturePublicKeyModel;
import authorization.dto.BlindSignaturePublicKeyResponse;
import authorization.mappers.BlindSignaturePublicKeyResponseMapper;
import authorization.services.BlindSignaturePublicKeyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@RequestMapping("/api/authorization-service/elections/{electionId}/keys")
@RestController
@RequiredArgsConstructor
public class BlindSignaturePublicKeyController {
    private final BlindSignaturePublicKeyService blindSignaturePublicKeyService;
    private final BlindSignaturePublicKeyResponseMapper mapper;

    @PostMapping("")
    public ResponseEntity<BlindSignaturePublicKeyResponse> create(@Valid @PathVariable UUID electionId) {
        BlindSignaturePublicKeyModel publicKeyModel = 
            blindSignaturePublicKeyService.generateKeys(electionId);

        BlindSignaturePublicKeyResponse response = mapper.toResponse(publicKeyModel);
        return ResponseEntity
            .status(HttpStatus.CREATED).body(response);
    }

    
    @GetMapping("/public")
    public ResponseEntity<BlindSignaturePublicKeyResponse> getPublicKeyByElectionId(@Valid @PathVariable UUID electionId) {
        BlindSignaturePublicKeyModel publicKeyModel = blindSignaturePublicKeyService.getByElectionId(electionId);
        BlindSignaturePublicKeyResponse response = mapper.toResponse(publicKeyModel);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/activate")
    public ResponseEntity<BlindSignaturePublicKeyResponse> activateKeys(@Valid @PathVariable UUID electionId) {
        BlindSignaturePublicKeyModel model = blindSignaturePublicKeyService.activateKeys(electionId);
        BlindSignaturePublicKeyResponse response = mapper.toResponse(model);
        return ResponseEntity.ok(response);
    }
}
