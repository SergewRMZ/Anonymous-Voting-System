package com.voting_system.tally_service.client.dto;
import java.util.List;
import java.util.UUID;

public record GetElectionStructureDto (
    UUID electionId,
    List<ElectionPositionDto> positions
) {
    public record ElectionPositionDto(
        UUID electionPositionId,
        List<UUID> candidacies
    ) {}
}
