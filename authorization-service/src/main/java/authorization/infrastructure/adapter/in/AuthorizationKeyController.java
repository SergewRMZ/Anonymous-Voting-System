package authorization.infrastructure.adapter.in;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;

import authorization.application.port.in.CreateAuthorizationKeysUseCase;
import authorization.application.port.in.GetAuthorizationPublicKeyUseCase;
import authorization.domain.model.AuthorizationPublicKeyModel;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


@RestController
@RequestMapping("/elections/{electionId}/authorization-keys")
@RequiredArgsConstructor
public class AuthorizationKeyController {
    private final CreateAuthorizationKeysUseCase createAuthorizationKeysUseCase;
    private final GetAuthorizationPublicKeyUseCase getAuthorizationPublicKeyUseCase;

    @PostMapping
    public ResponseEntity<AuthorizationKeyPairResponse> create(@Valid @PathVariable UUID electionId) {
        AuthorizationPublicKeyModel saved = createAuthorizationKeysUseCase.generateKeyPair(electionId);
        return ResponseEntity.status(HttpStatus.CREATED).body(AuthorizationKeyPairResponse.from(saved));
    }

    
    @GetMapping()
    public ResponseEntity getPublicKeyByElectionId(@Valid @PathVariable UUID electionId) {
        AuthorizationPublicKeyModel publicKey = getAuthorizationPublicKeyUseCase.getByElectionId(electionId);
        return ResponseEntity.ok(AuthorizationKeyPairResponse.from(publicKey));
    }
}
