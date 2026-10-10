package authorization.services.interfaces;

import java.util.UUID;

import authorization.domain.model.BlindSignaturePublicKeyModel;

public interface IBlindSignaturePublicKeyService {
    public void loadActiveKeys();
    public BlindSignaturePublicKeyModel generateKeys(UUID electionId);
    public BlindSignaturePublicKeyModel getByElectionId(UUID electionId);
    public BlindSignaturePublicKeyModel activateKeys(UUID electionId);
}
