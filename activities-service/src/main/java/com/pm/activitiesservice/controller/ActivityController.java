package com.pm.activitiesservice.controller;

import com.pm.activitiesservice.dto.ActivityRequestDTO;
import com.pm.activitiesservice.dto.ActivityResponseDTO;
import com.pm.activitiesservice.model.Activity;
import com.pm.activitiesservice.service.ActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/activities")
@RequiredArgsConstructor
public class ActivityController {
    private final ActivityService activityService;

    @PostMapping("/createActivity")
    public ResponseEntity<ActivityResponseDTO> createActivity(@RequestBody ActivityRequestDTO body) {
        return ResponseEntity.ok(activityService.createActivity(body));
    }

    @GetMapping("/")
    public ResponseEntity<List<ActivityResponseDTO>> getAllActivities() {
        return ResponseEntity.ok(activityService.getAllActivities());
    }

    @GetMapping(value = "/", params = "userID")
    public ResponseEntity<List<ActivityResponseDTO>> getAllActivitiesByUserID(@RequestParam UUID userID) {
        return ResponseEntity.ok(activityService.getAllActivitiesByUserID(userID));
    }

    @GetMapping("/{activityID}")
    public ResponseEntity<ActivityResponseDTO> getActivity(@PathVariable("activityID") String activityID) {
        return ResponseEntity.ok(activityService.getActivityByID(activityID));
    }
}
