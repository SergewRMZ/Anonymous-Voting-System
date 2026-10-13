package com.voting_system.tally_service.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigInteger;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.bouncycastle.asn1.sec.SECNamedCurves;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.math.ec.ECPoint;
import org.junit.jupiter.api.Test;

import com.voting_system.tally_service.client.BulletinBoardClient;
import com.voting_system.tally_service.client.BulletinBoardClient.BallotDto;
import com.voting_system.tally_service.client.BulletinBoardClient.CiphertextDto;
import com.voting_system.tally_service.client.BulletinBoardClient.EncryptedVoteDto;
import com.voting_system.tally_service.client.BulletinBoardClient.PositionDto;
import com.voting_system.tally_service.client.ElectionServiceClient;
import com.voting_system.tally_service.client.ElectionStatusResponse;
import com.voting_system.tally_service.model.EncryptionKeyModel;
import com.voting_system.tally_service.model.KeyStatus;
import com.voting_system.tally_service.repository.EncryptionKeyRepositoryAdapter;
import com.voting_system.tally_service.repository.PrivateEncryptionKeyRepository;

class TallyServiceTest {
    @Test
    void sumsEncryptedBinaryVotesAndDecryptsOnlyTheAggregate() {
        UUID electionId = UUID.randomUUID();
        UUID positionId = UUID.randomUUID();
        UUID candidacyId = UUID.randomUUID();
        BigInteger privateScalar = BigInteger.valueOf(7);
        X9ECParameters curve = SECNamedCurves.getByName("secp256k1");
        ECPoint publicKey = curve.getG().multiply(privateScalar);

        EncryptedVoteDto yesVote = vote(positionId, candidacyId, encryptBit(1, 3, publicKey, curve));
        EncryptedVoteDto noVote = vote(positionId, candidacyId, encryptBit(0, 5, publicKey, curve));

        BulletinBoardClient bulletinBoardClient = mock(BulletinBoardClient.class);
        ElectionServiceClient electionServiceClient = mock(ElectionServiceClient.class);
        EncryptionKeyRepositoryAdapter keyRepository = mock(EncryptionKeyRepositoryAdapter.class);
        PrivateEncryptionKeyRepository privateKeyRepository = mock(PrivateEncryptionKeyRepository.class);

        when(electionServiceClient.getStatus(electionId))
            .thenReturn(new ElectionStatusResponse(electionId, "CLOSED", false));
        when(keyRepository.findByElectionId(electionId))
            .thenReturn(Optional.of(EncryptionKeyModel.builder().status(KeyStatus.ACTIVE).build()));
        when(privateKeyRepository.readPrivateKey(electionId)).thenReturn(privateScalar.toByteArray());
        when(bulletinBoardClient.getEncryptedVotes(electionId)).thenReturn(List.of(yesVote, noVote));

        TallyService tallyService = new TallyService(
            bulletinBoardClient,
            electionServiceClient,
            keyRepository,
            privateKeyRepository
        );

        var result = tallyService.tally(electionId);

        assertEquals(electionId, result.electionId());
        assertEquals(2, result.ballotsProcessed());
        assertEquals(1, result.results().size());
        assertEquals(positionId, result.results().getFirst().electionPositionId());
        assertEquals(candidacyId, result.results().getFirst().candidacyId());
        assertEquals(1, result.results().getFirst().votes());
    }

    private EncryptedVoteDto vote(UUID positionId, UUID candidacyId, CiphertextDto ciphertext) {
        return new EncryptedVoteDto(
            UUID.randomUUID(),
            List.of(new PositionDto(positionId, List.of(new BallotDto(candidacyId, ciphertext))))
        );
    }

    private CiphertextDto encryptBit(
        int bit,
        int randomizer,
        ECPoint publicKey,
        X9ECParameters curve
    ) {
        ECPoint c1 = curve.getG().multiply(BigInteger.valueOf(randomizer)).normalize();
        ECPoint c2 = curve.getG().multiply(BigInteger.valueOf(bit))
            .add(publicKey.multiply(BigInteger.valueOf(randomizer)))
            .normalize();
        return new CiphertextDto(
            Base64.getEncoder().encodeToString(c1.getEncoded(true)),
            Base64.getEncoder().encodeToString(c2.getEncoded(true))
        );
    }
}
