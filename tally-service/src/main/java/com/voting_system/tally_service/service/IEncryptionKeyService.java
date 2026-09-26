package com.voting_system.tally_service.service;

import java.util.UUID;
import com.voting_system.tally_service.dto.UpdateEncryptionKeyStatusDto;
import com.voting_system.tally_service.model.EncryptionKeyModel;

public interface IEncryptionKeyService {
    public EncryptionKeyModel createEncryptionKeys(UUID electionId); 
    public EncryptionKeyModel setEncryptionKeyStatus(UUID electionId, UpdateEncryptionKeyStatusDto status); 
    public EncryptionKeyModel getPublicEncryptionKey(UUID electionId);
}
