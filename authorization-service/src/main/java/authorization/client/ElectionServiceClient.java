package authorization.client;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import authorization.domain.exception.ElectionStateUnavailableException;

/**
 * Consulta el estado de una elección en election-service, por la red interna.
 * Se usa para no firmar fuera del periodo de votación y para expirar las claves
 * de las elecciones que ya cerraron.
 */
@Component
public class ElectionServiceClient {
    private final RestClient restClient;

    public ElectionServiceClient(@Value("${services.election-service.base-url}") String electionServiceBaseUrl) {
        this.restClient = RestClient.builder().baseUrl(electionServiceBaseUrl).build();
    }

    public ElectionStatus getElectionStatus(UUID electionId) {
        try {
            ElectionStateResponse response = restClient.get()
                .uri("/api/election-service/elections/{electionId}/state", electionId)
                .retrieve()
                .body(ElectionStateResponse.class);

            if (response == null || response.status() == null) {
                throw new ElectionStateUnavailableException(electionId);
            }
            return response.status();
        } catch (RestClientException ex) {
            throw new ElectionStateUnavailableException(electionId, ex);
        }
    }
}
