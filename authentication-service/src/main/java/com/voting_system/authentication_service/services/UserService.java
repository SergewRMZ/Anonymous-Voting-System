package com.voting_system.authentication_service.services;

import java.util.Collections;

import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.voting_system.authentication_service.dto.UserLoginRequestDTO;
import com.voting_system.authentication_service.dto.UserRegisterRequestDTO;

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

    public String registerUser(UserRegisterRequestDTO request) {
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(request.password());
        credential.setTemporary(false);

        UserRepresentation user = new UserRepresentation();
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setEnabled(true);
        user.setCredentials(Collections.singletonList(credential));

        try (Response response = keycloak.realm(this.realm).users().create(user)) {
            if(response.getStatus() == 201) {
                String location = response.getHeaderString("Location");
                return location.substring(location.lastIndexOf('/') + 1);
            }   
            else if(response.getStatus() == 400) {
                String errorMessage = response.readEntity(String.class);
                throw new RuntimeException("Keycloak http error 400: " + errorMessage);
            }
            else {
                throw new RuntimeException("Error during user creation in keycloak. HTTP code: " + response.getStatus());
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
}
