package authorization.dto;

import jakarta.validation.constraints.NotBlank;
public record BlindSignatureRequest(
    @NotBlank(message = "Blinded message in Base64 format is required")
    String blindedMessage
) {}