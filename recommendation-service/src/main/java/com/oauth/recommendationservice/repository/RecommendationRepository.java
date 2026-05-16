package com.oauth.recommendationservice.repository;

import com.oauth.recommendationservice.model.Recommendation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RecommendationRepository extends MongoRepository<Recommendation, UUID> {
    List<Recommendation> findByUserId(UUID userId);
    List<Recommendation> findByActivityId(UUID activityId);
}
