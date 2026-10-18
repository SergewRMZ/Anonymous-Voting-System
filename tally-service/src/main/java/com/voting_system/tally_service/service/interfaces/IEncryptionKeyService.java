package com.voting_system.tally_service.service.interfaces;

import java.util.UUID;
import com.voting_system.tally_service.model.EncryptionKeyModel;

public interface IEncryptionKeyService {
    public EncryptionKeyModel createEncryptionKeys(UUID electionId); 
    public EncryptionKeyModel activateEncryptionKeys(UUID electionId); 
    public EncryptionKeyModel getPublicEncryptionKey(UUID electionId);

}
