package com.voting_system.tally_service.crypto;

import org.bouncycastle.asn1.sec.SECNamedCurves;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.math.ec.ECPoint;
import org.springframework.stereotype.Component;

import com.voting_system.tally_service.exception.InvalidEllipticCurvePointsException;

@Component 
public class ElGamalAcumulator {
    private final X9ECParameters params;

    private ECPoint accumulatedC1;
    private ECPoint accumulatedC2;

    public ElGamalAcumulator() {
        this.params = SECNamedCurves.getByName("secp256k1");
        resetAccumulator();
    }

    public void resetAccumulator() {
        this.accumulatedC1 = params.getCurve().getInfinity();
        this.accumulatedC2 = params.getCurve().getInfinity();
    }

    public void add(byte[] c1, byte[] c2) {
        ECPoint pointC1 = params.getCurve().decodePoint(c1).normalize();
        ECPoint pointC2 = params.getCurve().decodePoint(c2).normalize();

        // Verifica si ambos puntos pertenecen a la curva elíptica
        if(!pointC1.isValid() || !pointC2.isValid()) {
            throw new InvalidEllipticCurvePointsException();
        }

        accumulatedC1 = accumulatedC1.add(pointC1);
        accumulatedC2 = accumulatedC2.add(pointC2);
    }

    public String getAccumulatedC1() {
        return CryptoUtils.bytesToBase64(accumulatedC1.getEncoded(true));
    }

    public String getAccumulatedC2() {
        return CryptoUtils.bytesToBase64(accumulatedC2.getEncoded(true));
    }
}
