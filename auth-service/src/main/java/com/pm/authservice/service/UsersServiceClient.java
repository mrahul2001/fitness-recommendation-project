package com.pm.authservice.service;

import com.pm.authservice.dto.UserServiceRequestDTO;
import com.pm.authservice.dto.UserServiceResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "users-service",
        url = "http://localhost:4001"
)
public interface UsersServiceClient {

    @GetMapping("/api/users/email/{email}")
    UserServiceResponseDTO getUserByEmail(
            @PathVariable("email")
            String email
    );

    @PostMapping("/api/users/find-or-create")
    UserServiceResponseDTO findOrCreateGoogleUser(
            @RequestBody
            UserServiceRequestDTO request
    );
}
