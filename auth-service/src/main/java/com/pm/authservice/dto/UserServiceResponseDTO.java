package com.pm.authservice.dto;

import com.pm.authservice.model.AuthProvider;
import com.pm.authservice.model.UserRole;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class UserServiceResponseDTO {

    private UUID id;
    private String email;
    private String firstName;
    private String lastName;
    private String password;
    private UserRole userRole;
    private AuthProvider authProvider;
    private String providerId;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
}