package com.pm.gateway.config;

import com.pm.gateway.dto.LoginRequestDTO;
import com.pm.gateway.dto.UserResponseDTO;
import com.pm.gateway.service.JwtService;
import lombok.RequiredArgsConstructor;
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

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements ServerAuthenticationSuccessHandler {

    private final WebClient.Builder webClientBuilder;
    private final JwtService jwtService;


    @Override
    public Mono<Void> onAuthenticationSuccess(WebFilterExchange exchange, Authentication authentication) {

        OAuth2User user = (OAuth2User) authentication.getPrincipal();

        LoginRequestDTO dto = new LoginRequestDTO();
        dto.setEmail(user.getAttribute("email"));
        dto.setPassword(user.getAttribute("password"));
        dto.setProviderId(user.getAttribute("sub"));

        return webClientBuilder.build()
                .post()
                .uri("http://localhost:4001/api/users/find-or-create")
                .bodyValue(dto)
                .retrieve()
                .bodyToMono(UserResponseDTO.class)
                .flatMap(savedUser-> {
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
                });
    }
}
