package com.pm.authservice.dto;

import com.pm.authservice.model.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TokenValidationResponseDTO {

    private boolean valid;

    private String email;

    private UserRole role;

    private String userId;
}