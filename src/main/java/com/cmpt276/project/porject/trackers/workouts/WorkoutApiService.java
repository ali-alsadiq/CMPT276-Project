package com.cmpt276.project.porject.trackers.workouts;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.json.JSONArray;
import org.json.JSONObject;

@Service
public class WorkoutApiService {
    private static final String API_URL = "https://api.api-ninjas.com/v1/caloriesburned";
    private static final String API_KEY = "JsA3JsxqkwdiDwYXtz5PzA1LX1U0INdNJBvtPGO7";

    public int getCaloriesBurned(String activity, int duration) {
        RestTemplate restTemplate = new RestTemplate();
        
        try {
            // add api key to headers
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-Api-Key", API_KEY);
            
            
            // Build URL
            String url = API_URL + "?activity=" + activity + "&duration=" + duration;
            
            // create http entity with headers
            HttpEntity<?> entity = new HttpEntity<>(headers);
            
            // json response
            ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                String.class
            );
            
            // Turn into JSONarray
            JSONArray jsonArray = new JSONArray(response.getBody());
            
            if (jsonArray.length() > 0) {
                // Get first item in the array and get total_calories
                JSONObject firstItem = jsonArray.getJSONObject(0);
                return firstItem.getInt("total_calories");
            }
            
        } catch (Exception e) {
            System.err.println("Error fetching calories: " + e.getMessage());
        }
        
        return 0;
    }
}