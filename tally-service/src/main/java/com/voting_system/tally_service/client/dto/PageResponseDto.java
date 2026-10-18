package com.voting_system.tally_service.client.dto;

import java.util.List;

public record PageResponseDto<T>(
    List<T> content,
    int number,
    int size,
    int totalElements,
    int totalPages,
    boolean first,
    boolean last,
    boolean empty
) {}
