package authorization.application.port.in;

import java.util.UUID;

import authorization.domain.model.AuthorizedVoterModel;

public interface GenerateBlindSignatureUseCase {
    public AuthorizedVoterModel generateBlindSignature(UUID electionId, String message);
}
