package keys.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JwkResponseDto {
    String kty;
    String kid;
    String use;
    String n;
    String e;
}
