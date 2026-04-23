package com.pm.usersservice.dto;

import lombok.Data;

@Data
public class FindOrCreateUserDTO {
    private String email;
    private String firstName;
    private String lastName;
    private String googleId;
}
