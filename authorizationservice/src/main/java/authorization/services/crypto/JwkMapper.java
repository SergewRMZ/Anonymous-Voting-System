package authorization.services.crypto;

import java.security.interfaces.RSAPublicKey;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;

public class JwkMapper {
    private JwkMapper() {};
    public static RSAKey toJwk(RSAPublicKey pk) {
        try {
            return new RSAKey.Builder(pk)
                .keyUse(KeyUse.SIGNATURE)
                .keyIDFromThumbprint()
                .build();
        } catch (JOSEException e) {
            throw new RuntimeException("Error al mapear la clave pública", e);
        }
    }
}
