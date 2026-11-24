package authorization.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import authorization.client.ElectionServiceClient;
import authorization.client.ElectionStatus;
import authorization.domain.model.BlindSignaturePublicKeyModel;
import authorization.services.interfaces.IBlindSignaturePublicKeyService;
import lombok.RequiredArgsConstructor;

/**
 * Expira las claves de firma ciega de las elecciones que ya cerraron.
 *
 * Se ejecuta desde que arranca el servicio, a intervalos fijos: revisa solo las claves
 * ACTIVE y, si su elección está CLOSED o FINISHED, las pasa a EXPIRED. Si el servicio
 * se reinicia, la siguiente revisión expira lo que haya quedado pendiente.
 */
@Component
@RequiredArgsConstructor
public class AuthorizationKeyExpirationScheduler {
    private static final Logger log = LoggerFactory.getLogger(AuthorizationKeyExpirationScheduler.class);

    private final IBlindSignaturePublicKeyService blindSignaturePublicKeyService;
    private final ElectionServiceClient electionServiceClient;

    @Scheduled(fixedDelayString = "${authorization.keys.expiration-check-interval-ms}")
    public void expireKeysOfClosedElections() {
        for (BlindSignaturePublicKeyModel key : blindSignaturePublicKeyService.getActiveKeys()) {
            try {
                ElectionStatus status = electionServiceClient.getElectionStatus(key.getElectionId());
                if (status.votingEnded()) {
                    blindSignaturePublicKeyService.expireActiveElectionKeys(key.getElectionId());
                    log.info("Authorization keys of election {} expired (election {})", key.getElectionId(), status);
                }
            } catch (RuntimeException ex) {
                // Una elección que falla no detiene la revisión de las demás; se reintenta en la siguiente.
                log.warn("Could not check the authorization keys of election {}: {}", key.getElectionId(), ex.getMessage());
            }
        }
    }
}
