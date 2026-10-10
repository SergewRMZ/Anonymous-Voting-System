package authorization.mappers;

import java.security.interfaces.RSAPublicKey;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;

import lombok.RequiredArgsConstructor;


@Component
@RequiredArgsConstructor 
public class JwkMapper {
    private final RsaKeyMapper rsaKeyMapper;
    public Map<String, Object> bytesToJwk(byte[] publicKeyBytes) {
        try {
            RSAPublicKey pk = (RSAPublicKey) this.rsaKeyMapper.bytesToRsaPublicKey(publicKeyBytes);
            RSAKey pkJwk = new RSAKey.Builder(pk)
                .keyUse(KeyUse.SIGNATURE)
                .keyIDFromThumbprint()
                .build();
            return pkJwk.toPublicJWK().toJSONObject();
        } catch (JOSEException e) {
            throw new RuntimeException("Error al mapear la clave pública", e);
        }
    }
}
