package com.cmpt276.project.porject.trackers.workouts;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Service to interface with api-ninjas excersise api
 */
@Service
public class WorkoutApiService {
    private static final String API_URL = "https://api.api-ninjas.com/v1/caloriesburned";
    private static final String API_KEY = "JsA3JsxqkwdiDwYXtz5PzA1LX1U0INdNJBvtPGO7";

    /**
     * Gets the workout described and returns the estimated calories
     * 
     * @param activity the workout performed
     * @param duration length in minutes of workout
     * @return returns workout object storing calorie info, null if api request failed or was invalid
     */
    public Workout getWorkout(String activity, int duration) {
        RestTemplate restTemplate = new RestTemplate();
        
        try {
            // add api key to headers
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-Api-Key", API_KEY);
            
            //ensure proper format
            String encoded = java.net.URLEncoder.encode(activity, "UTF-8");
            String url = API_URL + "?activity=" + encoded + "&duration=" + duration;
            
            //make request
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                String.class
            );
            
            // conv to json array
            JSONArray jsonArray = new JSONArray(response.getBody());
            
            if (jsonArray.length() > 0) {
                // Get first item in the array and get total_calories
                JSONObject firstItem = jsonArray.getJSONObject(0);
                int calories = firstItem.getInt("total_calories");

                Workout workout = new Workout(activity, duration, calories, LocalDateTime.now());
                return workout;
            }
            
        } catch (Exception e) {
            System.err.println("Error fetching calories for:" + e.getMessage());
        }
        
        return null;
    }
}