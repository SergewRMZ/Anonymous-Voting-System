package com.voting_system.authentication_service.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.voting_system.authentication_service.model.UserRole;

import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;

/**
 * Acceso a los usuarios y roles del realm mediante la API de administración de Keycloak.
 * Aísla las llamadas remotas para que las reglas de aprobación puedan probarse sin Keycloak.
 */
@Component
@RequiredArgsConstructor
public class KeycloakUserDirectory {
    private static final int PAGE_SIZE = 500;

    private final Keycloak keycloak;

    @Value("${keycloak.realm}")
    private String realm;

    /** Todos los usuarios del realm, con sus atributos (representación completa). */
    public List<UserRepresentation> findAllUsers() {
        List<UserRepresentation> users = new ArrayList<>();
        for (int first = 0; ; first += PAGE_SIZE) {
            List<UserRepresentation> page = keycloak.realm(realm).users()
                .search(null, first, PAGE_SIZE, false);
            users.addAll(page);
            if (page.size() < PAGE_SIZE) return users;
        }
    }

    /** Identificadores de los usuarios con el rol de realm asignado directamente. */
    public Set<String> findUserIdsWithRole(UserRole role) {
        Set<String> ids = new HashSet<>();
        for (int first = 0; ; first += PAGE_SIZE) {
            List<UserRepresentation> page = keycloak.realm(realm).roles().get(role.name())
                .getUserMembers(true, first, PAGE_SIZE);
            page.forEach(user -> ids.add(user.getId()));
            if (page.size() < PAGE_SIZE) return ids;
        }
    }

    public Optional<UserRepresentation> findUser(String userId) {
        try {
            return Optional.of(keycloak.realm(realm).users().get(userId).toRepresentation());
        } catch (NotFoundException ex) {
            return Optional.empty();
        }
    }

    /** Roles de realm efectivos del usuario (incluye los heredados por grupos o roles compuestos). */
    public Set<String> findEffectiveRoleNames(String userId) {
        Set<String> names = new HashSet<>();
        keycloak.realm(realm).users().get(userId).roles().realmLevel().listEffective()
            .forEach(role -> names.add(role.getName()));
        return names;
    }

    public void assignRealmRole(String userId, UserRole role) {
        RoleRepresentation representation = keycloak.realm(realm).roles().get(role.name()).toRepresentation();
        keycloak.realm(realm).users().get(userId).roles().realmLevel()
            .add(Collections.singletonList(representation));
    }
}
