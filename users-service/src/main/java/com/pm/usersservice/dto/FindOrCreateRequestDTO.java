package com.pm.usersservice.dto;

import com.pm.usersservice.model.AuthProvider;
import lombok.Data;

@Data
public class FindOrCreateRequestDTO {
    private String email;
    private String firstName;
    private String lastName;
    private AuthProvider AuthProvider;
    private String providerId;
}
