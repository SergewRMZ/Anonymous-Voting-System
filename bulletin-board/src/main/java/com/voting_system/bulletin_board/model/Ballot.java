package com.voting_system.bulletin_board.model;

import java.util.UUID;

public record Ballot (
    UUID candidacyId,
    ElGamalCiphertext vote
) {}
