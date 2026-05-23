package com.pm.gateway.controller;

import com.pm.gateway.dto.LoginRequestDTO;
import com.pm.gateway.dto.UserResponseDTO;
import com.pm.gateway.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Slf4j
@RestController
@RequestMapping("/api/gateway")
@RequiredArgsConstructor
public class GatewayController {

    private final WebClient.Builder webClientBuilder;
    private final JwtService jwtService;

    @Value("${services.user-service.url}")
    private String userServiceUrl;

    @PostMapping("/login")
    public Mono<ResponseEntity<String>> login(@RequestBody LoginRequestDTO body, ServerHttpResponse response) {

        log.info("Login attempt — email: {}", body.getEmail());

        return webClientBuilder.build()
                .post()
                .uri(userServiceUrl + "/api/users/validate-login")
                .bodyValue(body)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, res -> {
                    log.warn("Login failed — email: {}, status: {}", body.getEmail(), res.statusCode());
                    return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));
                })
                .onStatus(HttpStatusCode::is5xxServerError, res -> {
                    log.error("User service error — email: {}, status: {}", body.getEmail(), res.statusCode());
                    return Mono.error(new ResponseStatusException(HttpStatus.BAD_GATEWAY, "User service unavailable"));
                })
                .bodyToMono(UserResponseDTO.class)
                .map(user -> {
                    log.info("User validated — id: {}, email: {}", user.getId(), user.getEmail());

                    String token = jwtService.generateToken(user.getId().toString(), user.getEmail());

                    ResponseCookie cookie = ResponseCookie.from("accessToken", token)
                            .httpOnly(true)
                            .secure(false)
                            .path("/")
                            .maxAge(Duration.ofDays(1))
                            .sameSite("Lax")
                            .build();

                    response.addCookie(cookie);
                    log.info("accessToken cookie set — userId: {}", user.getId());

                    return ResponseEntity.ok("Login successful");
                })
                .onErrorResume(ResponseStatusException.class, ex -> {
                    log.warn("Returning error — status: {}, reason: {}", ex.getStatusCode(), ex.getReason());
                    return Mono.just(ResponseEntity.status(ex.getStatusCode()).body(ex.getReason()));
                });
    }
}