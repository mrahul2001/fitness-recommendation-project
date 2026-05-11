package com.pm.activitiesservice.service;

import com.pm.activitiesservice.dto.ActivityRequestDTO;
import com.pm.activitiesservice.dto.ActivityResponseDTO;
import com.pm.activitiesservice.exception.ActivityNotFoundException;
import com.pm.activitiesservice.exception.UserNotFoundException;
import com.pm.activitiesservice.model.Activity;
import com.pm.activitiesservice.repository.ActivityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActivityService {

    @Value("${rabbitmq.exchange.name}")
    private String exchange;
    @Value("${rabbitmq.routing.key}")
    private String routingKey;

    private final ActivityRepository activityRepository;
    private final UserValidationService userValidationService;
    private final RabbitTemplate rabbitTemplate;

    public ActivityResponseDTO createActivity(ActivityRequestDTO body) {
        boolean isValid = userValidationService.validateUser(body.getUserID());

        if (!isValid) {
            throw new UserNotFoundException("User not found");
        }

        Activity activity = Activity.builder()
                .userID(body.getUserID())
                .activityType(body.getActivityType())
                .durationInMinutes(body.getDurationInMinutes())
                .caloriesBurnt(body.getCaloriesBurnt())
                .startTime(body.getStartTime())
                .additionalMetrics(body.getAdditionalMetrics())
                .build();

        Activity savedActivity = activityRepository.save(activity);
        log.info("Publishing activity to RabbitMQ → exchange={}, routingKey={}", exchange, routingKey);
        rabbitTemplate.convertAndSend(exchange, routingKey, savedActivity);
        log.info("Message published to RabbitMQ");
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
