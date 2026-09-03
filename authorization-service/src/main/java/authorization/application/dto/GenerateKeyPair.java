package authorization.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@Builder
public class GenerateKeyPair {
    private final byte[] privateKey;
    private final byte[] publicKey;
}
