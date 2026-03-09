package com.cmpt276.project.porject.trackers.nutrition;

import java.time.LocalDateTime;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class FoodApiService {
    private static final String API_URL = "https://api.calorieninjas.com/v1/nutrition";
    private static final String API_KEY = "+QKQeZ1DTdFPA3U7tW0LFg==vvsZzkX3mQMWmRSn";

    public Food getFoodNutrition(String foodDescription) {
        RestTemplate restTemplate = new RestTemplate();

        try {
            // add api key to headers
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-Api-Key", API_KEY);

            String encoded = java.net.URLEncoder.encode(foodDescription, "UTF-8");
            String url = API_URL + "?query=" + encoded;

            HttpEntity<?> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    String.class);

            JSONObject jsonResponse = new JSONObject(response.getBody());
            JSONArray jsonArray = jsonResponse.getJSONArray("items");

            if (jsonArray.length() > 0) {
                // Get first item returned
                JSONObject target = jsonArray.getJSONObject(0);

                // Get info
                String name = target.getString("name");
                Double calories = target.getDouble("calories");
                Double servSize = target.getDouble("serving_size_g");
                Double protien = target.getDouble("protein_g");
                Double carbs = target.getDouble("carbohydrates_total_g");
                Double fats = target.getDouble("fat_total_g");
                Double fiber = target.getDouble("fiber_g");
                Double sugar = target.getDouble("sugar_g");
                Double sodium = target.getDouble("sodium_mg");
                Double potassium = target.getDouble("potassium_mg");
                Double cholesterol = target.getDouble("cholesterol_mg");

                // Create food obj
                Food nutrition = new Food(name, calories, servSize, protien, carbs, fats, fiber, sugar, sodium,
                        potassium, cholesterol, LocalDateTime.now());
                return nutrition;
            }

        } catch (Exception e) {
            System.err.println("Error fetching calories: " + e.getMessage());
        }

        return null;
    }
}
