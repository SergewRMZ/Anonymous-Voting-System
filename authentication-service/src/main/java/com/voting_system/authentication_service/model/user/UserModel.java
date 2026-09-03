package com.voting_system.authentication_service.model.user;
import java.time.Instant;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class UserModel {
    private UUID id;
    private UUID rol_id;
    private String email;
    private String password;
    private String name;
    private UserStatus status;
    private Instant created_at;
}