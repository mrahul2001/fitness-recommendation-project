package com.pm.activitiesservice.service;

import com.pm.activitiesservice.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserValidationService {
    private final WebClient userServiceWebClient;

    public boolean validateUser(UUID userId) {

        try {
            Boolean response = userServiceWebClient
                    .get()
                    .uri("/api/users/{userId}/validate", userId)
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .block();

            log.info("Validation response = {}", response);
            return Boolean.TRUE.equals(response);
        } catch (Exception e) {
            log.error("Validation failed", e);
            throw e;
        }
    }
}
