package com.voting_system.election_service.domain;

import java.util.UUID;

public record CandidatePhotoModel (
    UUID candidateId,
    String contentType,
    String sha256,
    byte[] data
) {
}
