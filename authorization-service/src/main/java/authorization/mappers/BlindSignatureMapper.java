package authorization.mappers;

import org.springframework.stereotype.Component;

import authorization.domain.model.BlindSignatureModel;
import authorization.entity.JpaBlindSignatureEntity;
import authorization.entity.JpaBlindSignaturePublicKeyEntity;

@Component 
public class BlindSignatureMapper {
    public JpaBlindSignatureEntity toEntity(BlindSignatureModel model, JpaBlindSignaturePublicKeyEntity entity) {
        return JpaBlindSignatureEntity.builder()
            .id(model.getId())
            .electionId(model.getElectionId())
            .userId(model.getUserId())
            .authorizationKeysId(entity)
            .blindSignature(model.getBlindSignature())
            .createdAt(model.getCreatedAt())
            .build();
    }

    public BlindSignatureModel toModel(JpaBlindSignatureEntity entity) {
        return BlindSignatureModel.builder()
            .id(entity.getId())
            .electionId(entity.getElectionId())
            .userId(entity.getUserId())
            .authorizationKeysId(entity.getAuthorizationKeysId().getId())
            .blindSignature(entity.getBlindSignature())
            .createdAt(entity.getCreatedAt())
            .build();
    }
}
