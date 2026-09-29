package authorization.mappers;

import org.springframework.stereotype.Component;

import authorization.domain.model.BlindSignaturePublicKeyModel;
import authorization.entity.JpaBlindSignaturePublicKeyEntity;

@Component
public class BlindSignaturePublicKeyMapper {
    public BlindSignaturePublicKeyModel toModel(JpaBlindSignaturePublicKeyEntity entity) {
        if(entity == null) return null;
        return BlindSignaturePublicKeyModel.builder()
            .id(entity.getId())
            .electionId(entity.getElectionId())
            .publicKey(entity.getPublicKey())
            .status(entity.getStatus())
            .createdAt(entity.getCreatedAt())
            .activatedAt(entity.getActivatedAt())
            .expiredAt(entity.getExpiredAt())
            .build();
    }

    public JpaBlindSignaturePublicKeyEntity toEntity(BlindSignaturePublicKeyModel model) {
        if(model == null) return null;
        return JpaBlindSignaturePublicKeyEntity.builder()
            .id(model.getId())
            .electionId(model.getElectionId())
            .publicKey(model.getPublicKey())
            .status(model.getStatus())
            .createdAt(model.getCreatedAt())
            .activatedAt(model.getActivatedAt())
            .expiredAt(model.getExpiredAt())
            .build();
    }
}
