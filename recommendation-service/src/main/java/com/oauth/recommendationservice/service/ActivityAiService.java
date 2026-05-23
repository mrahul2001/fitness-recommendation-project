package com.oauth.recommendationservice.service;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import com.oauth.recommendationservice.model.Activity;
import com.oauth.recommendationservice.model.Recommendation;
import com.oauth.recommendationservice.repository.RecommendationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActivityAiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.model}")
    private String model;

    private final RecommendationRepository recommendationRepository;

    public void generateRecommendation(Activity activity) {

        try {
            log.info("Starting recommendation generation");

            String prompt = buildPrompt(activity);

            log.info("Generated Prompt:\n{}", prompt);
            String aiResponse = geminiResponse(prompt);
            log.info("Generated Response: \n{}", aiResponse);
            processAiResponse(activity, aiResponse);

        } catch (Exception e) {
            log.error("Failed generating recommendation", e);
        }
    }

    private void processAiResponse(Activity activity, String aiResponse) {
        ObjectMapper objectMapper = new ObjectMapper();

        int start = aiResponse.indexOf("{");
        int end = aiResponse.lastIndexOf("}");

        if (start == -1 || end == -1) {
            throw new RuntimeException("No JSON in AI Response");
        }

        String extractedJSON = aiResponse.substring(start, end + 1);
        JsonNode root = objectMapper.readTree(extractedJSON);

        String overallAnalysis = root.path("analysis").path("overall").asText();

        List<String> improvements = new ArrayList<>();
        root.path("improvements").forEach(node ->
                improvements.add(node.path("area").asText() + ": " + node.path("recommendation").asText())
        );

        List<String> suggestions = new ArrayList<>();
        root.path("suggestions").forEach(node ->
                suggestions.add(node.path("workout").asText() + ": " + node.path("description").asText())
        );

        List<String> safety = new ArrayList<>();
        root.path("safety").forEach(node ->
                safety.add(node.asText())
        );

        Recommendation recommendation = Recommendation.builder()
                .activityId(activity.getID())
                .userId(activity.getUserID().toString())
                .activityType(activity.getActivityType())
                .recommendationText(overallAnalysis)
                .improvements(improvements)
                .suggestions(suggestions)
                .safeties(safety)
                .build();

        recommendationRepository.save(recommendation);
    }

    private String geminiResponse(String prompt) {
        Client client = Client.builder()
                .apiKey(apiKey)
                .build();


        GenerateContentResponse response =
                client.models.generateContent(
                        model,
                        prompt,
                        null);

        return response.text();
    }

    private String buildPrompt(Activity activity) {

        return String.format("""
            A user completed the following workout activity.

            Analyze the workout performance and generate personalized fitness recommendations.

            Activity Details:
            - Activity Type: %s
            - Duration: %d minutes
            - Calories Burned: %d
            - Pace: %s
            - Distance: %s
            - Heart Rate: %s
            - Steps: %s

            Based on this data:
            1. Analyze the user's workout performance.
            2. Identify areas of improvement.
            3. Suggest next recommended workouts or activities.
            4. Provide important safety recommendations.

            Return ONLY valid JSON in the exact structure below.
            Do not include markdown, explanations, or extra text (Response should strictly be in JSON Format).

            {
              "analysis": {
                "overall": "overall analysis here",
                "pace": "pace analysis here",
                "heartRate": "heart rate analysis here",
                "caloriesBurnt": "calories burnt analysis here"
              },
              "improvements": [
                {
                  "area": "area name",
                  "recommendation": "detailed recommendation"
                }
              ],
              "suggestions": [
                {
                  "workout": "workout name",
                  "description": "detailed workout description"
                }
              ],
              "safety": [
                "safety point 1",
                "safety point 2",
                "safety point 3"
              ]
            }
            """,
                activity.getActivityType(),
                activity.getDurationInMinutes(),
                activity.getCaloriesBurnt(),
                activity.getAdditionalMetrics().get("pace"),
                activity.getAdditionalMetrics().get("distance"),
                activity.getAdditionalMetrics().get("heartRate"),
                activity.getAdditionalMetrics().get("steps")
        );
    }
}