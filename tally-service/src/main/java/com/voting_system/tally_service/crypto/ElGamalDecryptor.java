package com.voting_system.tally_service.crypto;

import java.math.BigInteger;

import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.asn1.sec.SECNamedCurves;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.springframework.stereotype.Component;

import com.voting_system.tally_service.exception.DiscreteLogNotFoundException;
import com.voting_system.tally_service.exception.InvalidEllipticCurvePointsException;

@Component 
public class ElGamalDecryptor {
    private final X9ECParameters params;

    public ElGamalDecryptor() {
        this.params = SECNamedCurves.getByName("secp256k1");
    }

    public int decrypt(byte[] c1, byte[] c2, BigInteger privateScalar, int maxVotes) {
        ECPoint pointC1 = params.getCurve().decodePoint(c1).normalize();
        ECPoint pointC2 = params.getCurve().decodePoint(c2).normalize();

        if(!pointC1.isValid() || !pointC2.isValid()) {
            throw new InvalidEllipticCurvePointsException();
        }

        ECPoint decryptedPoint = pointC2.subtract(pointC1.multiply(privateScalar)).normalize();
        return recoverPointScalar(decryptedPoint, maxVotes);
    }

    public int recoverPointScalar(ECPoint point, int maxVotes) {
        ECPoint generator = params.getG();
        ECPoint iterator = params.getCurve().getInfinity();

        for(int votes = 0; votes <= maxVotes; votes++) {
            if(iterator.equals(point)) {
                return votes;
            }

            iterator = iterator.add(generator);
        }

        throw new DiscreteLogNotFoundException();
    }
}
