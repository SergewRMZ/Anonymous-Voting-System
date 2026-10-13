package com.voting_system.tally_service.service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Comparator;

import org.bouncycastle.asn1.sec.SECNamedCurves;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.math.ec.ECPoint;
import org.springframework.stereotype.Service;

import com.voting_system.tally_service.client.BulletinBoardClient;
import com.voting_system.tally_service.client.BulletinBoardClient.BallotDto;
import com.voting_system.tally_service.client.BulletinBoardClient.EncryptedVoteDto;
import com.voting_system.tally_service.client.BulletinBoardClient.PositionDto;
import com.voting_system.tally_service.client.ElectionServiceClient;
import com.voting_system.tally_service.dto.TallyResultResponse;
import com.voting_system.tally_service.exception.ElectionNotClosedException;
import com.voting_system.tally_service.exception.EncryptionKeysNotFound;
import com.voting_system.tally_service.exception.InvalidCiphertextException;
import com.voting_system.tally_service.exception.InvalidKeyStateException;
import com.voting_system.tally_service.model.EncryptionKeyModel;
import com.voting_system.tally_service.model.KeyStatus;
import com.voting_system.tally_service.repository.EncryptionKeyRepositoryAdapter;
import com.voting_system.tally_service.repository.PrivateEncryptionKeyRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TallyService {
    private static final String CURVE_NAME = "secp256k1";

    private final BulletinBoardClient bulletinBoardClient;
    private final ElectionServiceClient electionServiceClient;
    private final EncryptionKeyRepositoryAdapter encryptionKeyRepository;
    private final PrivateEncryptionKeyRepository privateEncryptionKeyRepository;

    public TallyResultResponse tally(UUID electionId) {
        String electionStatus = electionServiceClient.getStatus(electionId).status();
        if (!"CLOSED".equals(electionStatus) && !"FINISHED".equals(electionStatus)) {
            throw new ElectionNotClosedException();
        }

        EncryptionKeyModel keyModel = encryptionKeyRepository.findByElectionId(electionId)
            .orElseThrow(() -> new EncryptionKeysNotFound(
                "Encryption keys not found for election with id: " + electionId
            ));
        if (keyModel.getStatus() != KeyStatus.ACTIVE) {
            throw new InvalidKeyStateException("Election encryption key must be ACTIVE to tally votes");
        }

        List<EncryptedVoteDto> votes = bulletinBoardClient.getEncryptedVotes(electionId);
        if (votes == null) {
            throw new IllegalStateException("Bulletin board returned no vote collection");
        }

        X9ECParameters curve = SECNamedCurves.getByName(CURVE_NAME);
        ECDomainParameters domain = new ECDomainParameters(curve);
        BigInteger privateScalar = new BigInteger(
            1,
            privateEncryptionKeyRepository.readPrivateKey(electionId)
        );
        if (privateScalar.signum() <= 0 || privateScalar.compareTo(domain.getN()) >= 0) {
            throw new IllegalStateException("Stored election private key is not a valid curve scalar");
        }

        Map<TallyKey, AggregateCiphertext> aggregates = new HashMap<>();
        for (EncryptedVoteDto vote : votes) {
            Set<TallyKey> seenInBallot = new HashSet<>();
            for (PositionDto position : vote.positions()) {
                for (BallotDto ballot : position.ballots()) {
                    TallyKey key = new TallyKey(position.electionPositionId(), ballot.candidacyId());
                    if (!seenInBallot.add(key)) {
                        throw new InvalidCiphertextException(
                            "A ballot contains the same candidacy more than once for an election position"
                        );
                    }
                    ECPoint c1 = decodePoint(ballot.vote().c1(), curve, domain, false);
                    ECPoint c2 = decodePoint(ballot.vote().c2(), curve, domain, true);
                    AggregateCiphertext aggregate = aggregates.computeIfAbsent(
                        key,
                        ignored -> new AggregateCiphertext(curve.getCurve().getInfinity(), curve.getCurve().getInfinity())
                    );
                    aggregate.add(c1, c2);
                }
            }
        }

        List<TallyResultResponse.CandidateTally> results = new ArrayList<>();
        for (Map.Entry<TallyKey, AggregateCiphertext> entry : aggregates.entrySet()) {
            ECPoint decryptedPoint = entry.getValue().c2
                .subtract(entry.getValue().c1.multiply(privateScalar))
                .normalize();
            int count = decodeCount(decryptedPoint, entry.getValue().ciphertextCount, curve);
            results.add(new TallyResultResponse.CandidateTally(
                entry.getKey().electionPositionId(),
                entry.getKey().candidacyId(),
                count
            ));
        }
        results.sort(
            Comparator.comparing(TallyResultResponse.CandidateTally::electionPositionId)
                .thenComparing(TallyResultResponse.CandidateTally::candidacyId)
        );

        return new TallyResultResponse(electionId, votes.size(), results);
    }

    private ECPoint decodePoint(
        String encodedPoint,
        X9ECParameters curve,
        ECDomainParameters domain,
        boolean allowInfinity
    ) {
        try {
            ECPoint point = curve.getCurve().decodePoint(Base64.getDecoder().decode(encodedPoint)).normalize();
            if ((!allowInfinity && point.isInfinity()) || !point.isValid()
                || !point.multiply(domain.getN()).isInfinity()) {
                throw new InvalidCiphertextException("Ciphertext contains an invalid elliptic-curve point");
            }
            return point;
        } catch (IllegalArgumentException exception) {
            throw new InvalidCiphertextException("Ciphertext point must be Base64-encoded SEC1 data", exception);
        }
    }

    private int decodeCount(ECPoint point, int maximumCount, X9ECParameters curve) {
        ECPoint candidate = curve.getCurve().getInfinity();
        for (int count = 0; count <= maximumCount; count++) {
            if (candidate.equals(point)) {
                return count;
            }
            candidate = candidate.add(curve.getG()).normalize();
        }
        throw new InvalidCiphertextException(
            "Decrypted tally is outside the valid range of 0 to " + maximumCount
        );
    }

    private record TallyKey(UUID electionPositionId, UUID candidacyId) {}

    private static final class AggregateCiphertext {
        private ECPoint c1;
        private ECPoint c2;
        private int ciphertextCount;

        private AggregateCiphertext(ECPoint c1, ECPoint c2) {
            this.c1 = c1;
            this.c2 = c2;
        }

        private void add(ECPoint nextC1, ECPoint nextC2) {
            c1 = c1.add(nextC1).normalize();
            c2 = c2.add(nextC2).normalize();
            ciphertextCount++;
        }
    }
}
