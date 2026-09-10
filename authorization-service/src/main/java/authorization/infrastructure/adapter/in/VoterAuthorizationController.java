package authorization.infrastructure.adapter.in;

import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import authorization.application.port.in.GenerateBlindSignatureUseCase;
import authorization.domain.model.AuthorizedVoterModel;
import authorization.infrastructure.adapter.in.dto.BlindSignatureResponse;
import authorization.infrastructure.adapter.in.dto.BlindSignatureRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController 
@RequiredArgsConstructor 
public class VoterAuthorizationController {
    private final GenerateBlindSignatureUseCase generateBlindSignatureUseCase;

    @PostMapping("/elections/{electionId}/authorize-vote")
    public ResponseEntity<BlindSignatureResponse> authorizeVote(@PathVariable UUID electionId, @Valid @RequestBody BlindSignatureRequest request) {
        AuthorizedVoterModel authorizedVoter = generateBlindSignatureUseCase.generateBlindSignature(electionId, request.getBlindedMessage());
        return ResponseEntity.status(HttpStatus.OK).body(BlindSignatureResponse.from(authorizedVoter));
    }
}
