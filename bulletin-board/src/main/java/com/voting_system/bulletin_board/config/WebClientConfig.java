package com.voting_system.bulletin_board.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration 
public class WebClientConfig {
    
    @Bean 
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }
}
