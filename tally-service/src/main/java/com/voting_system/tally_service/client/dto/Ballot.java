package com.voting_system.tally_service.client.dto;

import java.util.UUID;

public record Ballot (
    UUID candidacyId,
    ElGamalCiphertext vote
) {}
