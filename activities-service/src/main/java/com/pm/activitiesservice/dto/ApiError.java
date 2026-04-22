package com.pm.activitiesservice.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
public class ApiError {
    private int status;
    private String error;
    private String message;
    private String path;
    private LocalDateTime timestamp;
    private Map<String, String> validationErrors;
}
