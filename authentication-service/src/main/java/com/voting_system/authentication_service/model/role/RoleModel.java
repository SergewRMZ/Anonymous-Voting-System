package com.voting_system.authentication_service.model.role;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class RoleModel {
    private UUID id;
    private RoleType role;
}
