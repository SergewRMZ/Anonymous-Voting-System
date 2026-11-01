package com.voting_system.authentication_service.exceptions;

import lombok.Getter;

/**
 * La solicitud no puede aprobarse por el estado actual de la cuenta.
 * El código permite al cliente mostrar un mensaje específico.
 */
@Getter
public class VoterApprovalException extends RuntimeException {
    public enum Reason {
        ALREADY_APPROVED,
        PRIVILEGED_ACCOUNT,
        ACCOUNT_DISABLED,
        INVALID_MATRICULA,
        DUPLICATE_MATRICULA
    }

    private final Reason reason;

    public VoterApprovalException(Reason reason, String message) {
        super(message);
        this.reason = reason;
    }
}
