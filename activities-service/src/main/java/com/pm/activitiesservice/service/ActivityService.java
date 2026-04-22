package com.pm.activitiesservice.service;

import com.pm.activitiesservice.dto.ActivityRequestDTO;
import com.pm.activitiesservice.dto.ActivityResponseDTO;
import com.pm.activitiesservice.exception.ActivityNotFoundException;
import com.pm.activitiesservice.exception.UserNotFoundException;
import com.pm.activitiesservice.model.Activity;
import com.pm.activitiesservice.repository.ActivityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ActivityService {

    private final ActivityRepository activityRepository;

    public ActivityResponseDTO createActivity(ActivityRequestDTO body) {
        Activity activity = Activity.builder()
                .userID(body.getUserID())
                .activityType(body.getActivityType())
                .durationInMinutes(body.getDurationInMinutes())
                .caloriesBurnt(body.getCaloriesBurnt())
                .startTime(body.getStartTime())
                .additionalMetrics(body.getAdditionalMetrics())
                .build();

        Activity savedActivity = activityRepository.save(activity);

        return activityModelToResponseDTO(savedActivity);

    }

    private ActivityResponseDTO activityModelToResponseDTO(Activity savedActivity) {
        ActivityResponseDTO response = new ActivityResponseDTO();
        response.setID(savedActivity.getID());
        response.setUserID(savedActivity.getUserID());
        response.setActivityType(savedActivity.getActivityType());
        response.setDurationInMinutes(savedActivity.getDurationInMinutes());
        response.setCaloriesBurnt(savedActivity.getCaloriesBurnt());
        response.setStartTime(savedActivity.getStartTime());
        response.setAdditionalMetrics(savedActivity.getAdditionalMetrics());
        response.setCreatedAt(savedActivity.getCreatedAt());
        response.setUpdatedAt(savedActivity.getUpdatedAt());

        return response;
    }

    public List<ActivityResponseDTO> getAllActivities() {
        List<Activity> allActivities = activityRepository.findAll();

        return allActivities.stream()
                .map(this::activityModelToResponseDTO)
                .collect(Collectors.toList());
    }


    public List<ActivityResponseDTO> getAllActivitiesByUserID(UUID userID) {

        if (activityRepository.findByUserID(userID) == null) {
            throw new UserNotFoundException("User not found");
        }

        List<Activity> activities = activityRepository.findByUserID(userID);

        return activities.stream()
                .map(this::activityModelToResponseDTO)
                .collect(Collectors.toList());
    }

    public ActivityResponseDTO getActivityByID(String activityID) {
        Activity activity = activityRepository.findById(activityID).orElse(null);

        if (activity == null) {
            throw new ActivityNotFoundException("Activity " + activityID + " not found");
        }

        return activityModelToResponseDTO(activity);
    }
}
