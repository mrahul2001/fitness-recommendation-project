package com.pm.gateway.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final OAuth2SuccessHandler oAuth2SuccessHandler;

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchange -> {
                    exchange.pathMatchers(
                                    "/auth/**",
                                    "/api/gateway/**",
                                    "/oauth2/**",
                                    "/api/users/**",
                                    "/api/activities/**",
                                    "/api/recommendations/**"
                            )
                            .permitAll()
                            .anyExchange()
                            .authenticated();
                });
//                .oauth2Login(oauth2 -> {
//                    oauth2.authenticationSuccessHandler(oAuth2SuccessHandler);
//                });
        return http.build();
    }
}
