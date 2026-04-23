package com.pm.usersservice.dto;

import com.pm.usersservice.model.UserRole;
import lombok.Data;

import java.util.UUID;

@Data
public class UserAuthResponseDTO {
    private UUID id;
    private String email;
    private String firstName;
    private String lastName;
    private UserRole role;
    private String password;
    private String googleId;
}
