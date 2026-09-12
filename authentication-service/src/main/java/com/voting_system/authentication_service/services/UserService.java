package com.voting_system.authentication_service.services;

import java.util.Collections;

import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.voting_system.authentication_service.dto.UserLoginRequestDTO;
import com.voting_system.authentication_service.dto.UserRegisterRequestDTO;
import com.voting_system.authentication_service.model.UserRole;

import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor   
public class UserService {
    private final Keycloak keycloak;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.server.url}")
    private String serverUrl;

    @Value("${keycloak.client.id}")
    private String clientId;

    @Value("${keycloak.client.secret}")
    private String clientSecret;

    public void registerVoter(UserRegisterRequestDTO request) {
        createUserInKeycloak(request, false);
    }

    public void registerAdmin(UserRegisterRequestDTO request) {
        String userId = createUserInKeycloak(request, true);
        assignRole(userId, UserRole.ADMIN_ROLE);
    }

    public String createUserInKeycloak(UserRegisterRequestDTO request, boolean enabledUser) {
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(request.password());
        credential.setTemporary(false);

        UserRepresentation user = new UserRepresentation();
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setEnabled(enabledUser);
        user.setCredentials(Collections.singletonList(credential));

        try (Response response = keycloak
                .realm(this.realm)
                .users()
                .create(user)) {
            
            if(response.getStatus() == 201) {
                return extractUserId(response);
            }   

            else if(response.getStatus() == 409) {
                throw new RuntimeException("The email user is already registered");
            }

            else {
                throw new RuntimeException("Internal Server Error");
            }
        }
    }

    public AccessTokenResponse loginUser(UserLoginRequestDTO request) {
        try (Keycloak userKeycloak = KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm(realm)
                .grantType(OAuth2Constants.PASSWORD)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .username(request.username())
                .password(request.password())
                .build()) {
            return userKeycloak.tokenManager().getAccessToken();
        }
    }

    private String extractUserId(Response response) {
        return response.getLocation()
            .getPath()
            .substring(response.getLocation().getPath().lastIndexOf('/') + 1);
    }

    private void assignRole(String userId, UserRole roleName) {
        RoleRepresentation role = keycloak
            .realm(this.realm)
            .roles()
            .get(roleName.name())
            .toRepresentation();

        keycloak
            .realm(this.realm)
            .users()
            .get(userId)
            .roles()
            .realmLevel()
            .add(Collections.singletonList(role));
    }
}
