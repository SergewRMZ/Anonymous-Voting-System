package com.voting_system.api_gateway.config;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.config.Customizer;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import com.voting_system.api_gateway.enums.UserRole;

@Configuration 
@EnableWebFluxSecurity 
public class SecurityConfig {
    
    @Bean 
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity serverHttpSecurity) {
        return serverHttpSecurity
            // CSRF solo es necesaria cuando se trabaja con cookies
            .csrf(csrf -> csrf.disable())
            // El frontend se sirve desde otro dominio: usa corsConfigurationSource()
            .cors(Customizer.withDefaults())
            .authorizeExchange(exchanges -> exchanges

                // Tally Service
                .pathMatchers(
                    HttpMethod.GET, 
                    "/api/tally-service/elections/*/encryption-keys/public"
                ).permitAll()

                .pathMatchers(
                    HttpMethod.GET,
                    "/api/tally-service/elections/*/results"
                ).authenticated()

                .pathMatchers(
                    HttpMethod.POST, 
                    "/api/tally-service/elections/*/encryption-keys"
                ).hasRole(UserRole.AUTHORITY.name())

                .pathMatchers(
                    HttpMethod.POST,
                    "/api/tally-service/elections/*/tallies"
                ).hasRole(UserRole.AUTHORITY.name())

                .pathMatchers(
                    HttpMethod.PATCH,
                    "/api/tally-service/elections/*/encryption-keys/activate"
                ).hasRole(UserRole.AUTHORITY.name())

                .pathMatchers(
                    HttpMethod.PATCH,
                    "/api/tally-service/elections/*/encryption-keys/expire"
                ).hasRole(UserRole.AUTHORITY.name())

                // bulletin-board
                .pathMatchers(HttpMethod.POST, "/api/bulletin-board/elections/*/votes").permitAll()
                .pathMatchers(HttpMethod.GET, "/api/bulletin-board/elections/*/votes").permitAll()
                
                // authentication-service
                .pathMatchers(HttpMethod.POST, "/api/auth/admin").hasRole(UserRole.AUTHORITY.name())
                // Revisión de votantes registrados en Keycloak (listar, aprobar, habilitar/deshabilitar)
                .pathMatchers("/api/auth/voters", "/api/auth/voters/**").hasRole(UserRole.ADMIN.name())

                // authorization-service
                .pathMatchers(HttpMethod.GET, "/api/authorization-service/elections/*/keys/public").permitAll()
                .pathMatchers(HttpMethod.GET, "/api/authorization-service/elections/*/keys").hasAnyRole(UserRole.ADMIN.name(), UserRole.AUTHORITY.name())
                .pathMatchers(HttpMethod.POST, "/api/authorization-service/elections/*/keys").hasRole(UserRole.AUTHORITY.name())
                .pathMatchers(HttpMethod.PATCH, "/api/authorization-service/elections/*/keys/activate").hasRole(UserRole.AUTHORITY.name())
                .pathMatchers(HttpMethod.POST, "/api/authorization-service/elections/*/blind-signature").hasRole(UserRole.VOTER.name())
                // Si el votante del token ya obtuvo su firma ciega en la elección (ya votó).
                .pathMatchers(HttpMethod.GET, "/api/authorization-service/elections/*/blind-signature/me").hasRole(UserRole.VOTER.name())

                // election-service
                // La autoridad finaliza la elección después de contabilizar los votos.
                .pathMatchers(HttpMethod.PATCH, "/api/election-service/elections/*/finish").hasRole(UserRole.AUTHORITY.name())
                .pathMatchers(HttpMethod.POST, "/api/election-service/elections").hasRole(UserRole.ADMIN.name())
                .pathMatchers(HttpMethod.PUT, "/api/election-service/elections/*").hasRole(UserRole.ADMIN.name())
                .pathMatchers(HttpMethod.PATCH, "/api/election-service/elections/*/publish").hasRole(UserRole.ADMIN.name())
                .pathMatchers(HttpMethod.PATCH, "/api/election-service/elections/*/validate").hasRole(UserRole.AUTHORITY.name())
                .pathMatchers(HttpMethod.PATCH, "/api/election-service/elections/*/activate").hasRole(UserRole.AUTHORITY.name())
                .pathMatchers(HttpMethod.GET, "/api/election-service/elections").hasAnyRole(UserRole.VOTER.name(), UserRole.ADMIN.name(), UserRole.AUTHORITY.name())
                .pathMatchers(HttpMethod.GET, "/api/election-service/elections/{electionId}").hasAnyRole(UserRole.VOTER.name(), UserRole.ADMIN.name(), UserRole.AUTHORITY.name())

                .pathMatchers(HttpMethod.POST, "/api/election-service/positions").hasRole(UserRole.ADMIN.name())
                .pathMatchers(HttpMethod.POST, "/api/election-service/elections/*/positions/*").hasRole(UserRole.ADMIN.name())
                .pathMatchers(HttpMethod.GET, "/api/election-service/positions").hasRole(UserRole.ADMIN.name())
                
                .pathMatchers(HttpMethod.POST, "/api/election-service/candidates").hasRole(UserRole.ADMIN.name())
                .pathMatchers(HttpMethod.POST, "/api/election-service/election-positions/*/candidacies").hasRole(UserRole.ADMIN.name())
                .pathMatchers(HttpMethod.GET, "/api/election-service/candidates").hasRole(UserRole.ADMIN.name())
                .pathMatchers(HttpMethod.PUT, "/api/election-service/candidates/*/photo").hasRole(UserRole.ADMIN.name())
                // Pública: la carga un <img>, que no envía el token.
                .pathMatchers(HttpMethod.GET, "/api/election-service/candidates/*/photo").permitAll()
                .anyExchange().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwtSpec -> {
                jwtSpec.jwtAuthenticationConverter(reactiveJwtAuthenticationConverterAdapter());
            }))
            .build();
    }

    /**
     * Orígenes del frontend que pueden llamar a la API desde el navegador, separados por comas.
     * En local no hace falta: Vite reenvía /api al gateway desde el mismo origen.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource(
        @Value("${CORS_ALLOWED_ORIGINS:http://localhost:5173}") List<String> allowedOrigins
    ) {
        CorsConfiguration cors = new CorsConfiguration();
        cors.setAllowedOrigins(allowedOrigins);
        cors.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        cors.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        cors.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cors);
        return source;
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
