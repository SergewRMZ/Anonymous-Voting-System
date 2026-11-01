package com.voting_system.authentication_service.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.representations.idm.UserRepresentation;

import com.voting_system.authentication_service.dto.VoterRegistrationDto;
import com.voting_system.authentication_service.dto.VoterRegistrationPageDto;
import com.voting_system.authentication_service.exceptions.UserNotFoundException;
import com.voting_system.authentication_service.exceptions.VoterApprovalException;
import com.voting_system.authentication_service.exceptions.VoterApprovalException.Reason;
import com.voting_system.authentication_service.model.UserRole;
import com.voting_system.authentication_service.model.VoterRegistrationStatus;

class VoterRegistrationServiceTest {
    private KeycloakUserDirectory directory;
    private VoterRegistrationService service;

    @BeforeEach
    void setUp() {
        directory = mock(KeycloakUserDirectory.class);
        service = new VoterRegistrationService(directory);
        when(directory.findUserIdsWithRole(any())).thenReturn(Set.of());
        when(directory.findEffectiveRoleNames(anyString())).thenReturn(Set.of("default-roles-voting-system-realm"));
    }

    private static UserRepresentation user(String id, String matricula, long createdAt) {
        UserRepresentation user = new UserRepresentation();
        user.setId(id);
        user.setUsername("user-" + id);
        user.setEmail(id + "@example.com");
        user.setFirstName("Nombre " + id);
        user.setLastName("Apellido");
        user.setEnabled(true);
        user.setCreatedTimestamp(createdAt);
        if (matricula != null) user.setAttributes(Map.of("matricula", List.of(matricula)));
        return user;
    }

    @Test
    void validatesMatriculaAsExactlyTenDigits() {
        assertTrue(VoterRegistrationService.isValidMatricula("2026123456"));
        assertFalse(VoterRegistrationService.isValidMatricula("202612345"));
        assertFalse(VoterRegistrationService.isValidMatricula("20261234567"));
        assertFalse(VoterRegistrationService.isValidMatricula("20261234a6"));
        assertFalse(VoterRegistrationService.isValidMatricula(null));
    }

    @Test
    void classifiesPendingAndApprovedAndExcludesStaffAndServiceAccounts() {
        UserRepresentation pending = user("p", "1111111111", 3);
        UserRepresentation approved = user("v", "2222222222", 2);
        UserRepresentation admin = user("a", null, 1);
        UserRepresentation serviceAccount = user("s", null, 0);
        serviceAccount.setUsername("service-account-authentication-service-dev");

        when(directory.findAllUsers()).thenReturn(List.of(pending, approved, admin, serviceAccount));
        when(directory.findUserIdsWithRole(UserRole.ROLE_VOTER)).thenReturn(Set.of("v"));
        when(directory.findUserIdsWithRole(UserRole.ROLE_ADMIN)).thenReturn(Set.of("a"));

        VoterRegistrationPageDto all = service.findRegistrations(null, null, 0, 20);
        assertEquals(2, all.totalElements());
        assertEquals(1, all.pendingCount());
        assertEquals(1, all.approvedCount());
        // Los más recientes primero.
        assertEquals("p", all.content().get(0).userId());

        VoterRegistrationPageDto onlyPending = service.findRegistrations(VoterRegistrationStatus.PENDING, null, 0, 20);
        assertEquals(List.of("p"), onlyPending.content().stream().map(VoterRegistrationDto::userId).toList());
        // Los contadores no dependen del filtro aplicado.
        assertEquals(1, onlyPending.approvedCount());
    }

    @Test
    void searchesByMatriculaAndPaginates() {
        when(directory.findAllUsers()).thenReturn(List.of(
            user("1", "1000000001", 5), user("2", "1000000002", 4), user("3", "2000000003", 3)));

        VoterRegistrationPageDto byMatricula = service.findRegistrations(null, "2000000003", 0, 20);
        assertEquals(1, byMatricula.totalElements());
        assertEquals("3", byMatricula.content().get(0).userId());

        VoterRegistrationPageDto secondPage = service.findRegistrations(null, "10000", 1, 1);
        assertEquals(2, secondPage.totalElements());
        assertEquals("2", secondPage.content().get(0).userId());

        VoterRegistrationPageDto beyond = service.findRegistrations(null, null, 5, 20);
        assertTrue(beyond.content().isEmpty());
    }

    @Test
    void approvesPendingRegistrationByAssigningVoterRole() {
        UserRepresentation pending = user("p", "1234567890", 1);
        when(directory.findUser("p")).thenReturn(Optional.of(pending));
        when(directory.findAllUsers()).thenReturn(List.of(pending));

        VoterRegistrationDto result = service.approve("p");

        assertEquals(VoterRegistrationStatus.APPROVED, result.status());
        verify(directory).assignRealmRole("p", UserRole.ROLE_VOTER);
    }

    @Test
    void rejectsUnknownUser() {
        when(directory.findUser("x")).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> service.approve("x"));
    }

    @Test
    void rejectsAlreadyApprovedVoter() {
        when(directory.findUser("v")).thenReturn(Optional.of(user("v", "1234567890", 1)));
        when(directory.findEffectiveRoleNames("v")).thenReturn(Set.of("ROLE_VOTER"));
        assertReason(Reason.ALREADY_APPROVED, "v");
    }

    @Test
    void rejectsStaffAccounts() {
        when(directory.findUser("a")).thenReturn(Optional.of(user("a", "1234567890", 1)));
        when(directory.findEffectiveRoleNames("a")).thenReturn(Set.of("ROLE_AUTHORITY"));
        assertReason(Reason.PRIVILEGED_ACCOUNT, "a");
    }

    @Test
    void rejectsDisabledAccounts() {
        UserRepresentation disabled = user("d", "1234567890", 1);
        disabled.setEnabled(false);
        when(directory.findUser("d")).thenReturn(Optional.of(disabled));
        assertReason(Reason.ACCOUNT_DISABLED, "d");
    }

    @Test
    void rejectsMissingOrInvalidMatricula() {
        when(directory.findUser("n")).thenReturn(Optional.of(user("n", null, 1)));
        assertReason(Reason.INVALID_MATRICULA, "n");

        when(directory.findUser("i")).thenReturn(Optional.of(user("i", "12345", 1)));
        assertReason(Reason.INVALID_MATRICULA, "i");
    }

    @Test
    void rejectsMatriculaAlreadyUsedByAnApprovedVoter() {
        UserRepresentation candidate = user("p", "1234567890", 2);
        UserRepresentation existingVoter = user("v", "1234567890", 1);
        when(directory.findUser("p")).thenReturn(Optional.of(candidate));
        when(directory.findAllUsers()).thenReturn(List.of(candidate, existingVoter));
        when(directory.findUserIdsWithRole(UserRole.ROLE_VOTER)).thenReturn(Set.of("v"));

        assertReason(Reason.DUPLICATE_MATRICULA, "p");
    }

    @Test
    void allowsSameMatriculaWhenTheOtherAccountIsStillPending() {
        UserRepresentation candidate = user("p", "1234567890", 2);
        UserRepresentation otherPending = user("q", "1234567890", 1);
        when(directory.findUser("p")).thenReturn(Optional.of(candidate));
        when(directory.findAllUsers()).thenReturn(List.of(candidate, otherPending));

        service.approve("p");
        verify(directory).assignRealmRole("p", UserRole.ROLE_VOTER);
    }

    private void assertReason(Reason expected, String userId) {
        VoterApprovalException ex = assertThrows(VoterApprovalException.class, () -> service.approve(userId));
        assertEquals(expected, ex.getReason());
        verify(directory, never()).assignRealmRole(userId, UserRole.ROLE_VOTER);
    }
}
