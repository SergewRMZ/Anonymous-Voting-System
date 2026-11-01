package com.voting_system.authentication_service.services;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.voting_system.authentication_service.dto.VoterRegistrationDto;
import com.voting_system.authentication_service.dto.VoterRegistrationPageDto;
import com.voting_system.authentication_service.exceptions.UserNotFoundException;
import com.voting_system.authentication_service.exceptions.VoterApprovalException;
import com.voting_system.authentication_service.exceptions.VoterApprovalException.Reason;
import com.voting_system.authentication_service.model.UserRole;
import com.voting_system.authentication_service.model.VoterRegistrationStatus;

import lombok.RequiredArgsConstructor;

/**
 * Revisión y aprobación de los votantes que se registran desde Keycloak.
 * Un registro queda pendiente mientras no tenga ROLE_VOTER; aprobarlo consiste en asignarle ese rol.
 * Las cuentas de administración y de autoridad electoral no forman parte de este flujo.
 */
@Service
@RequiredArgsConstructor
public class VoterRegistrationService {
    private static final Pattern MATRICULA_PATTERN = Pattern.compile("^\\d{10}$");
    private static final String SERVICE_ACCOUNT_PREFIX = "service-account-";

    private final KeycloakUserDirectory userDirectory;

    @Value("${voters.matricula-attribute:matricula}")
    private String matriculaAttribute = "matricula";

    public static boolean isValidMatricula(String matricula) {
        return matricula != null && MATRICULA_PATTERN.matcher(matricula).matches();
    }

    public VoterRegistrationPageDto findRegistrations(VoterRegistrationStatus status, String search, int page, int size) {
        Set<String> voterIds = userDirectory.findUserIdsWithRole(UserRole.ROLE_VOTER);
        Set<String> adminIds = userDirectory.findUserIdsWithRole(UserRole.ROLE_ADMIN);
        Set<String> authorityIds = userDirectory.findUserIdsWithRole(UserRole.ROLE_AUTHORITY);

        List<VoterRegistrationDto> registrations = userDirectory.findAllUsers().stream()
            .filter(user -> !isServiceAccount(user))
            .filter(user -> !adminIds.contains(user.getId()) && !authorityIds.contains(user.getId()))
            .map(user -> toDto(user, voterIds.contains(user.getId())))
            .toList();

        long pendingCount = registrations.stream().filter(r -> r.status() == VoterRegistrationStatus.PENDING).count();
        long approvedCount = registrations.size() - pendingCount;

        String term = search == null ? "" : search.trim().toLowerCase(Locale.ROOT);
        List<VoterRegistrationDto> filtered = registrations.stream()
            .filter(r -> status == null || r.status() == status)
            .filter(r -> term.isEmpty() || matches(r, term))
            .sorted(Comparator.comparing(VoterRegistrationDto::registeredAt,
                Comparator.nullsLast(Comparator.reverseOrder())))
            .toList();

        int from = Math.min(page * size, filtered.size());
        int to = Math.min(from + size, filtered.size());
        return new VoterRegistrationPageDto(
            filtered.subList(from, to), page, size, filtered.size(), pendingCount, approvedCount);
    }

    public VoterRegistrationDto approve(String userId) {
        UserRepresentation user = userDirectory.findUser(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));
        Set<String> roles = userDirectory.findEffectiveRoleNames(userId);

        if (roles.contains(UserRole.ROLE_ADMIN.name()) || roles.contains(UserRole.ROLE_AUTHORITY.name())) {
            throw new VoterApprovalException(Reason.PRIVILEGED_ACCOUNT,
                "Administrative accounts are not managed through voter approval");
        }
        if (roles.contains(UserRole.ROLE_VOTER.name())) {
            throw new VoterApprovalException(Reason.ALREADY_APPROVED, "The voter has already been approved");
        }
        if (!Boolean.TRUE.equals(user.isEnabled())) {
            throw new VoterApprovalException(Reason.ACCOUNT_DISABLED, "Disabled accounts cannot be approved");
        }

        String matricula = matriculaOf(user);
        if (!isValidMatricula(matricula)) {
            throw new VoterApprovalException(Reason.INVALID_MATRICULA,
                "The account does not have a valid 10-digit matricula");
        }
        if (isMatriculaTakenByApprovedVoter(matricula, userId)) {
            throw new VoterApprovalException(Reason.DUPLICATE_MATRICULA,
                "Another approved voter already uses this matricula");
        }

        userDirectory.assignRealmRole(userId, UserRole.ROLE_VOTER);
        return toDto(user, true);
    }

    private boolean isMatriculaTakenByApprovedVoter(String matricula, String userId) {
        Set<String> voterIds = userDirectory.findUserIdsWithRole(UserRole.ROLE_VOTER);
        return userDirectory.findAllUsers().stream()
            .filter(other -> !other.getId().equals(userId) && voterIds.contains(other.getId()))
            .anyMatch(other -> matricula.equals(matriculaOf(other)));
    }

    private boolean isServiceAccount(UserRepresentation user) {
        return user.getUsername() != null && user.getUsername().startsWith(SERVICE_ACCOUNT_PREFIX);
    }

    private String matriculaOf(UserRepresentation user) {
        Map<String, List<String>> attributes = user.getAttributes();
        if (attributes == null) return null;
        List<String> values = attributes.get(matriculaAttribute);
        return values == null || values.isEmpty() ? null : values.get(0).trim();
    }

    private VoterRegistrationDto toDto(UserRepresentation user, boolean approved) {
        String matricula = matriculaOf(user);
        return new VoterRegistrationDto(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            Boolean.TRUE.equals(user.isEmailVerified()),
            user.getFirstName(),
            user.getLastName(),
            matricula,
            isValidMatricula(matricula),
            Boolean.TRUE.equals(user.isEnabled()),
            user.getCreatedTimestamp() == null ? null : Instant.ofEpochMilli(user.getCreatedTimestamp()),
            approved ? VoterRegistrationStatus.APPROVED : VoterRegistrationStatus.PENDING
        );
    }

    private boolean matches(VoterRegistrationDto registration, String term) {
        return Stream.of(registration.username(), registration.email(), registration.firstName(),
                registration.lastName(), registration.matricula())
            .filter(Objects::nonNull)
            .anyMatch(value -> value.toLowerCase(Locale.ROOT).contains(term));
    }
}
