package authorization.infrastructure.adapter.in.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import jakarta.validation.constraints.NotBlank;
@Getter 
@AllArgsConstructor 
public class BlindSignatureRequest {
    @NotBlank(message = "Blinded message in Base64 format is required")
    private final String blindedMessage;
}
