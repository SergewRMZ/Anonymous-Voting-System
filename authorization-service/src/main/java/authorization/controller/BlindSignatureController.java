package authorization.controller;

import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import authorization.domain.model.BlindSignatureModel;
import authorization.dto.AuthorizationStatusResponse;
import authorization.dto.BlindSignatureRequest;
import authorization.dto.BlindSignatureResponse;
import authorization.services.BlindSignatureService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController 
@RequiredArgsConstructor 
public class BlindSignatureController {
    private final BlindSignatureService blindSignatureService;

    @PostMapping("/api/authorization-service/elections/{electionId}/blind-signature")
    public ResponseEntity<BlindSignatureResponse> authorizeVote(
        @PathVariable UUID electionId, 
        @Valid @RequestBody BlindSignatureRequest request,
        @AuthenticationPrincipal Jwt jwt
    ) {
            UUID userId = UUID.fromString(jwt.getSubject());
            BlindSignatureModel blindSignatureModel = blindSignatureService.generateBlindSignature(electionId, userId, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(BlindSignatureResponse.from(blindSignatureModel));
    }

    /**
     * Indica si el votante autenticado ya solicitó la firma ciega en esta elección.
     * El votante se identifica con el sub del token, nunca con un parámetro de la petición,
     * para que nadie pueda consultar el estado de otro usuario.
     */
    @GetMapping("/api/authorization-service/elections/{electionId}/blind-signature/me")
    public ResponseEntity<AuthorizationStatusResponse> getMyAuthorizationStatus(
        @PathVariable UUID electionId,
        @AuthenticationPrincipal Jwt jwt
    ) {
        UUID userId = UUID.fromString(jwt.getSubject());
        boolean authorized = blindSignatureService.hasRequestedAuthorization(electionId, userId);
        return ResponseEntity.ok(new AuthorizationStatusResponse(electionId, authorized));
    }
}
