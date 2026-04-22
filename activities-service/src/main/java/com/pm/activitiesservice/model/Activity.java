package com.pm.activitiesservice.model;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Document(collection = "activities")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Activity {
    @Id
    private String ID;

    private UUID userID;

    private ActivityType activityType;

    private Integer durationInMinutes;

    private Integer caloriesBurnt;

    private LocalDateTime startTime;

    @Field("metrics")
    private Map<String, Object> additionalMetrics;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

}
