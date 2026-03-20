package com.cmpt276.project.porject.meals;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Service to interface with calorieninjas api
 */
@Service
public class FoodApiService {
    private static final String API_URL = "https://api.calorieninjas.com/v1/nutrition";
    private static final String API_KEY = "+QKQeZ1DTdFPA3U7tW0LFg==vvsZzkX3mQMWmRSn";

    /**
     * Gets the foods described and returns their nutrition info
     * 
     * @param foodDescription the natural description of the meal/foods
     * @return returns list of food objects, null if api request failed or was invalid
     */
    public List<Food> getMealNutrition(String foodDescription) {
        RestTemplate restTemplate = new RestTemplate();

        try {
            // add api key to headers
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-Api-Key", API_KEY);

            //ensure valid format
            String safeDescription = foodDescription.replace(",", " ");
            String encoded = java.net.URLEncoder.encode(safeDescription, "UTF-8");
            String url = API_URL + "?query=" + encoded;
            System.out.println("API URL: " + url);

            //make api request
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    String.class);

            //convert to json array of food items
            JSONObject jsonResponse = new JSONObject(response.getBody());
            JSONArray jsonArray = jsonResponse.getJSONArray("items");

            //Add all to list
            if (jsonArray.length() > 0) {
                List<Food> mealFoods = new ArrayList<>();

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject target = jsonArray.getJSONObject(i);

                    // Get info
                    String name = target.getString("name");
                    double calories = target.getDouble("calories");
                    double servSize = target.getDouble("serving_size_g");
                    double protien = target.getDouble("protein_g");
                    double carbs = target.getDouble("carbohydrates_total_g");
                    double fats = target.getDouble("fat_total_g");
                    double fiber = target.getDouble("fiber_g");
                    double sugar = target.getDouble("sugar_g");
                    double sodium = target.getDouble("sodium_mg");
                    double potassium = target.getDouble("potassium_mg");
                    double cholesterol = target.getDouble("cholesterol_mg");

                    // Create food obj
                    Food food = new Food(
                            name,
                            servSize,
                            calories,
                            protien,
                            carbs,
                            fats,
                            fiber,
                            sugar,
                            sodium,
                            potassium,
                            cholesterol);

                    mealFoods.add(food);
                }

                return mealFoods;
            }

        } catch (Exception e) {
            System.err.println("Error fetching nutrition for: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }
}
