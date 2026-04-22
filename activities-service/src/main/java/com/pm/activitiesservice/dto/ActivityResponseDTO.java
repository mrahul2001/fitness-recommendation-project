package com.pm.activitiesservice.dto;

import com.pm.activitiesservice.model.ActivityType;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Data
public class ActivityResponseDTO {
    private String ID;
    private UUID userID;
    private ActivityType activityType;
    private Integer durationInMinutes;
    private Integer caloriesBurnt;
    private LocalDateTime startTime;
    private Map<String, Object> additionalMetrics;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
