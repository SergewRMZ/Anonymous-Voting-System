package com.voting_system.authentication_service.dto;

import java.util.List;

public record VoterRegistrationPageDto(
    List<VoterRegistrationDto> content,
    int page,
    int size,
    long totalElements,
    long pendingCount,
    long approvedCount
) {}
