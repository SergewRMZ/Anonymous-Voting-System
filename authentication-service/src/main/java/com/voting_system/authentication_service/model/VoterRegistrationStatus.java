package com.voting_system.authentication_service.model;

/**
 * Estado de un registro de votante.
 * PENDING: cuenta creada por autoregistro en Keycloak, sin ROLE_VOTER.
 * APPROVED: un administrador revisó el registro y le asignó ROLE_VOTER.
 */
public enum VoterRegistrationStatus {
    PENDING,
    APPROVED
}
