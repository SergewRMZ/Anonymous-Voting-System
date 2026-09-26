package authorization.infrastructure.adapter.in;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;

import authorization.application.port.in.ActiveKeyUseCase;
import authorization.application.port.in.CreateAuthorizationKeysUseCase;
import authorization.application.port.in.GetAuthorizationPublicKeyUseCase;
import authorization.domain.model.ElectionPublicKeyModel;

import authorization.infrastructure.adapter.in.dto.ElectionPublicKeyResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@RequestMapping("/api/elections/{electionId}/keys")
@RestController
@RequiredArgsConstructor
public class ElectionKeyController {
    private final CreateAuthorizationKeysUseCase createAuthorizationKeysUseCase;
    private final GetAuthorizationPublicKeyUseCase getAuthorizationPublicKeyUseCase;
    private final ActiveKeyUseCase activeKeyUseCase;


    @PostMapping("")
    public ResponseEntity<ElectionPublicKeyResponse> create(@Valid @PathVariable UUID electionId) {
        ElectionPublicKeyModel saved = createAuthorizationKeysUseCase.generateKeyPair(electionId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ElectionPublicKeyResponse.from(saved));
    }

    
    @GetMapping("/public")
    public ResponseEntity<ElectionPublicKeyResponse> getPublicKeyByElectionId(@Valid @PathVariable UUID electionId) {
        ElectionPublicKeyModel publicKey = getAuthorizationPublicKeyUseCase.getByElectionId(electionId);
        return ResponseEntity.ok(ElectionPublicKeyResponse.from(publicKey));
    }

    @PatchMapping("/activate")
    public ResponseEntity<ElectionPublicKeyResponse> activateKey(@Valid @PathVariable UUID electionId) {
        ElectionPublicKeyModel publicKeyModel = activeKeyUseCase.activateKey(electionId);
        return ResponseEntity.ok(ElectionPublicKeyResponse.from(publicKeyModel));
    }
}
