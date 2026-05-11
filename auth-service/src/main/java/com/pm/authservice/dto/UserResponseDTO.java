package com.pm.authservice.dto;

import com.pm.authservice.model.AuthProvider;
import com.pm.authservice.model.UserRole;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class UserResponseDTO {

    private UUID id;
    private String email;
    private String firstName;
    private String lastName;
    private UserRole userRole;
    private AuthProvider authProvider;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
}