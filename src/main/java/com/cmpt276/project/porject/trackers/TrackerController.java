package com.cmpt276.project.porject.trackers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.cmpt276.project.porject.trackers.nutrition.Food;
import com.cmpt276.project.porject.trackers.nutrition.FoodApiService;
import com.cmpt276.project.porject.trackers.nutrition.FoodRepository;
import com.cmpt276.project.porject.trackers.workouts.Workout;
import com.cmpt276.project.porject.trackers.workouts.WorkoutApiService;
import com.cmpt276.project.porject.trackers.workouts.WorkoutRepository;

/**
 * Controller for nutrition and workout management
 */
@Controller
public class TrackerController {
    @Autowired
    private WorkoutApiService workoutApiService;

    @Autowired
    private FoodApiService foodApiService;

    @Autowired
    private WorkoutRepository workoutRepository;

    @Autowired
    private FoodRepository foodRepository;

    // Add workout page
    @GetMapping("/add-workout")
    public String showAddWorkout() {
        return "add-workout";
    }

    @GetMapping("/add-food")
    public String showAddFood() {
        return "add-food";
    }

    
    @PostMapping("/add-food")
    public String postMethodName(@RequestParam String foodDescription, Model model) {
        Food food = foodApiService.getFoodNutrition(foodDescription);

        if (food == null) {
            model.addAttribute("messageType", "error");
            System.err.println("Failed to find calories burned for: " + food);
        } else {
            model.addAttribute("nutrition", food);
            foodRepository.save(food);
        }

        // SENDS BACK TO FORM FOR NOW FOR TESTING
        return "add-food";
    }

    
    @PostMapping("/add-workout")
    public String addWorkout(@RequestParam String activity, int duration, Model model) {
        Workout workout = workoutApiService.getWorkout(activity, duration);

        //Check valid obj
        if (workout == null) {
            model.addAttribute("messageType", "error");
            System.err.println("Failed to find calories burned for: " + activity);

        } else {
            model.addAttribute("workout", workout);
            workoutRepository.save(workout);
        }

        // SENDS BACK TO FORM FOR NOW FOR TESTING
        return "add-workout";
    }
}
