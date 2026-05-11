package com.pm.authservice.dto;

import com.pm.authservice.model.AuthProvider;
import com.pm.authservice.model.UserRole;
import lombok.Data;
import java.util.UUID;

@Data
public class UserServiceRequestDTO {
    private String email;
    private UserRole userRole;
    private AuthProvider authProvider;
    private String providerId;
    private String firstName;
    private String lastName;
}
