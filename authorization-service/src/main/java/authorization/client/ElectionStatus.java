package authorization.client;

/** Estados de una elección, tal como los devuelve election-service. */
public enum ElectionStatus {
    DRAFT,
    PUBLISHED,
    VERIFIED,
    ACTIVE,
    CLOSED,
    FINISHED;

    /** La votación terminó: ya no se deben emitir firmas para la elección. */
    public boolean votingEnded() {
        return this == CLOSED || this == FINISHED;
    }
}
