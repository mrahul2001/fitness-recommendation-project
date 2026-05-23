package com.pm.gateway.config;

import com.pm.gateway.dto.LoginRequestDTO;
import com.pm.gateway.dto.UserResponseDTO;
import com.pm.gateway.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.Duration;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements ServerAuthenticationSuccessHandler {

    private final WebClient.Builder webClientBuilder;
    private final JwtService jwtService;

    @Override
    public Mono<Void> onAuthenticationSuccess(WebFilterExchange exchange, Authentication authentication) {

        OAuth2User user = (OAuth2User) authentication.getPrincipal();

        String email = user.getAttribute("email");
        String providerId = user.getAttribute("sub");

        log.info("OAuth2 login success — email: {}, providerId: {}", email, providerId);

        LoginRequestDTO dto = new LoginRequestDTO();
        dto.setEmail(email);
        dto.setProviderId(providerId);

        return webClientBuilder.build()
                .post()
                .uri("http://users-service/api/users/find-or-create")
                .bodyValue(dto)
                .retrieve()
                .bodyToMono(UserResponseDTO.class)
                .flatMap(savedUser -> {
                    log.info("User resolved — id: {}, email: {}", savedUser.getId(), savedUser.getEmail());

                    String token = jwtService.generateToken(savedUser.getId().toString(), savedUser.getEmail());

                    ResponseCookie responseCookie = ResponseCookie.from("accessToken", token)
                            .httpOnly(true)
                            .secure(false)
                            .path("/")
                            .maxAge(Duration.ofDays(1))
                            .sameSite("Lax")
                            .build();

                    exchange.getExchange().getResponse().addCookie(responseCookie);
                    exchange.getExchange().getResponse().setStatusCode(HttpStatus.FOUND);
                    exchange.getExchange().getResponse().getHeaders().setLocation(URI.create("http://localhost:3000/home"));

                    return exchange.getExchange().getResponse().setComplete();
                })
                .doOnError(e -> log.error("OAuth2 flow failed — email: {}, error: {}", email, e.getMessage()));
    }
}