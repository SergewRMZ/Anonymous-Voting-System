package authorization.infrastructure.adapter.out;

import org.springframework.stereotype.Component;

import authorization.domain.model.AuthorizationPublicKeyModel;

@Component
public class AuthorizationPublicKeyMapper {
    public AuthorizationPublicKeyModel toDomain(JpaAuthorizationKeysEntity entity) {
        if(entity == null) return null;
        return AuthorizationPublicKeyModel.builder()
            .authorizationKeysId(entity.getAuthorizationKeyId())
            .electionId(entity.getElectionId())
            .publicKey(entity.getPublicKey())
            .status(entity.getStatus())
            .createdAt(entity.getCreatedAt())
            .activatedAt(entity.getActivatedAt())
            .expiredAt(entity.getExpiredAt())
            .build();
    }

    public JpaAuthorizationKeysEntity toEntity(AuthorizationPublicKeyModel model) {
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
