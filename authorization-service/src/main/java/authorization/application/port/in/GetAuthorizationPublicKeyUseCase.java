package authorization.application.port.in;

import java.util.UUID;

import authorization.domain.model.AuthorizationPublicKeyModel;

public interface GetAuthorizationPublicKeyUseCase {
    AuthorizationPublicKeyModel getByElectionId(UUID electionId);
}
