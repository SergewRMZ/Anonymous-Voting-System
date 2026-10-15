package com.voting_system.tally_service.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.RequiredArgsConstructor;

@Component 
@RequiredArgsConstructor 
public class BulletinBoardServiceClient {
    private final WebClient.Builder webClientBuilder;

    @Value("${services.bulletin-board-service.base-url}")
    private String bulletinBoardServiceBaseUrl;
}
