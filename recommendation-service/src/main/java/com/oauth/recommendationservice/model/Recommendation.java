package com.oauth.recommendationservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Document(collection = "fitness-recommendations")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Recommendation {
    @Id
    private String id;

    private String activityId;
    private String userId;
    private ActivityType activityType;
    private String recommendationText;
    private List<String> improvements;
    private List<String> suggestions;
    private List<String> safeties;

    @CreatedDate
    private LocalDateTime createdAt;
}
