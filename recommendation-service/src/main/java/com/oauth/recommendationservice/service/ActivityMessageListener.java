package com.oauth.recommendationservice.service;

import com.oauth.recommendationservice.model.Activity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ActivityMessageListener {
    private final ActivityAiService activityAiService;

    @RabbitListener(queues = "#{activityQueue.name}")
    public void receiveMessage(Activity activity) {
        try {
            log.info("Received Message from RabbitMQ::: {}", activity.toString());
            activityAiService.generateRecommendation(activity);
        } catch (Exception e) {
            log.error("Failed to generate Recommendation, due to \n {}", e.getMessage());
        }
    }

}
