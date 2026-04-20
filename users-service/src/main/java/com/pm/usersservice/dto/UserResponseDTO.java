package com.pm.usersservice.dto;

import com.pm.usersservice.model.UserRole;
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
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
}
