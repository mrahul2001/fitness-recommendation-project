package com.pm.activitiesservice.repository;

import com.pm.activitiesservice.dto.ActivityResponseDTO;
import com.pm.activitiesservice.model.Activity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ActivityRepository extends MongoRepository<Activity, String> {
    List<Activity> findByUserID(UUID userID);
}
