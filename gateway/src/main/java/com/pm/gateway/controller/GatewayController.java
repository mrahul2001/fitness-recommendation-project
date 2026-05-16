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

@RestController
@RequestMapping("/api/gateway")
@RequiredArgsConstructor
@Slf4j
public class GatewayController {

    private final WebClient.Builder webClientBuilder;
    private final JwtService jwtService;

    @Value("${services.user-service.url}")
    private String userServiceUrl;

    @PostMapping("/login")
    public Mono<ResponseEntity<String>> login(
            @RequestBody LoginRequestDTO body,
            ServerHttpResponse response) {

        log.info("Login attempt for email: {}", body.getEmail());

        return webClientBuilder.build()
                .post()
                .uri(userServiceUrl + "/api/users/validate-login")
                .bodyValue(body)
                .retrieve()
                .onStatus(
                        HttpStatusCode::is4xxClientError,
                        res -> {
                            log.info("Login failed for email: {} — status: {}", body.getEmail(), res.statusCode());
                            return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));
                        }
                )
                .onStatus(
                        HttpStatusCode::is5xxServerError,
                        res -> {
                            log.info("User service error for email: {} — status: {}", body.getEmail(), res.statusCode());
                            return Mono.error(new ResponseStatusException(HttpStatus.BAD_GATEWAY, "User service unavailable"));
                        }
                )
                .bodyToMono(UserResponseDTO.class)
                .map(user -> {
                    log.info("User validated successfully — id: {}, email: {}", user.getId(), user.getEmail());

                    String token = jwtService.generateToken(
                            user.getId().toString(),
                            user.getEmail()
                    );
                    log.info("JWT generated for user id: {}", user.getId());

                    ResponseCookie cookie = ResponseCookie.from("accessToken", token)
                            .httpOnly(true)
                            .secure(false)
                            .path("/")
                            .maxAge(Duration.ofDays(1))
                            .sameSite("Lax")
                            .build();

                    response.addCookie(cookie);
                    log.info("accessToken cookie set for user id: {}", user.getId());

                    return ResponseEntity.ok("Login Successful");
                })
                .onErrorResume(ResponseStatusException.class, ex -> {
                    log.info("Returning error response — status: {}, reason: {}", ex.getStatusCode(), ex.getReason());
                    return Mono.just(ResponseEntity.status(ex.getStatusCode())
                            .body(ex.getReason()));
                });
    }
}