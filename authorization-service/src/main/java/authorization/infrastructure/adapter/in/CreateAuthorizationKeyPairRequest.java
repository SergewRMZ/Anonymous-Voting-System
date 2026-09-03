package authorization.infrastructure.adapter.in;

import java.util.UUID;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateAuthorizationKeyPairRequest {
    @NotNull(message = "Election ID must be provided")
    private UUID electionId;
}
