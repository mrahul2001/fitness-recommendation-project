package com.pm.activitiesservice.dto;

import com.pm.activitiesservice.model.ActivityType;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Data
public class ActivityRequestDTO {
    private UUID userID;
    private ActivityType activityType;
    private Integer durationInMinutes;
    private Integer caloriesBurnt;
    private Map<String, Object> additionalMetrics;
    private LocalDateTime startTime;
}
