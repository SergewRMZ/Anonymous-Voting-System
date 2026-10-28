package authorization.services.interfaces;

import java.util.UUID;

import authorization.domain.model.BlindSignaturePublicKeyModel;

public interface IBlindSignaturePublicKeyService {
    public void loadPrivateKeyByAuthorizationKeysId(UUID authorizationKeysId);
    public BlindSignaturePublicKeyModel generateKeys(UUID electionId);
    public BlindSignaturePublicKeyModel getActivePublicKeyByElectionId(UUID electionId);
    public BlindSignaturePublicKeyModel activateElectionKeys(UUID electionId);
    public BlindSignaturePublicKeyModel expireActiveElectionKeys(UUID electionId);
}
