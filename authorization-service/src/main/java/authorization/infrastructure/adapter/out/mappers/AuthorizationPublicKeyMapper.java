package authorization.infrastructure.adapter.out.mappers;

import org.springframework.stereotype.Component;

import authorization.domain.model.ElectionPublicKeyModel;
import authorization.infrastructure.adapter.out.jpa.JpaAuthorizationKeysEntity;

@Component
public class AuthorizationPublicKeyMapper {
    public ElectionPublicKeyModel toDomain(JpaAuthorizationKeysEntity entity) {
        if(entity == null) return null;
        return ElectionPublicKeyModel.builder()
            .authorizationKeysId(entity.getAuthorizationKeyId())
            .electionId(entity.getElectionId())
            .publicKey(entity.getPublicKey())
            .status(entity.getStatus())
            .createdAt(entity.getCreatedAt())
            .activatedAt(entity.getActivatedAt())
            .expiredAt(entity.getExpiredAt())
            .build();
    }

    public JpaAuthorizationKeysEntity toEntity(ElectionPublicKeyModel model) {
        if(model == null) return null;
        return JpaAuthorizationKeysEntity.builder()
            .authorizationKeyId(model.getAuthorizationKeysId())
            .electionId(model.getElectionId())
            .publicKey(model.getPublicKey())
            .status(model.getStatus())
            .createdAt(model.getCreatedAt())
            .activatedAt(model.getActivatedAt())
            .expiredAt(model.getExpiredAt())
            .build();
    }
}
