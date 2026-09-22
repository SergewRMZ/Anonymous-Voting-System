package com.voting_system.api_gateway.config;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;

import com.voting_system.api_gateway.enums.UserRole;

@Configuration 
@EnableWebFluxSecurity 
public class SecurityConfig {
    @Bean 
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity serverHttpSecurity) {
        return serverHttpSecurity
            // CSRF solo es necesaria cuando se trabaja con cookies
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
            .authorizeExchange(exchanges -> exchanges

                // Public endpoints
                .pathMatchers(HttpMethod.POST, "/api/auth/voter").permitAll()
                .pathMatchers(HttpMethod.GET, "/api/elections/*/keys/public").permitAll()

                // Authenticated endpoints
                .pathMatchers(HttpMethod.POST, "/api/auth/admin").hasRole(UserRole.AUTHORITY.name())
                .pathMatchers(HttpMethod.PATCH, "/api/users/voter/*/status").hasRole(UserRole.ADMIN.name())

                // authorization-service
                .pathMatchers(HttpMethod.POST, "/api/elections/*/keys/*").hasRole(UserRole.AUTHORITY.name())
                .pathMatchers(HttpMethod.POST, "/api/elections/*/blind-signature").hasRole(UserRole.VOTER.name())

                // election-service
                .pathMatchers(HttpMethod.POST, "/api/election").hasRole(UserRole.ADMIN.name())
                .anyExchange().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwtSpec -> {
                jwtSpec.jwtAuthenticationConverter(reactiveJwtAuthenticationConverterAdapter());
            }))
            .build();
    }

    private ReactiveJwtAuthenticationConverterAdapter reactiveJwtAuthenticationConverterAdapter () {
        JwtAuthenticationConverter jwtConverter = new JwtAuthenticationConverter();

        jwtConverter.setJwtGrantedAuthoritiesConverter(jwt -> {
            Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");
            if(realmAccess == null || !realmAccess.containsKey("roles")){
                return Collections.emptyList();
            }

            @SuppressWarnings ("unchecked")
            Collection<String> roles = (Collection<String>) realmAccess.get("roles");

            return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role))
                .collect(Collectors.toList());
        });

        return new ReactiveJwtAuthenticationConverterAdapter(jwtConverter);
    }
}
