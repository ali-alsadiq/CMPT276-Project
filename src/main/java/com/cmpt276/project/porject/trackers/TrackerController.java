package com.cmpt276.project.porject.trackers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.cmpt276.project.porject.auth.User;
import com.cmpt276.project.porject.auth.UserRepository;
import com.cmpt276.project.porject.trackers.nutrition.Food;
import com.cmpt276.project.porject.trackers.nutrition.FoodApiService;
import com.cmpt276.project.porject.trackers.nutrition.FoodRepository;
import com.cmpt276.project.porject.trackers.workouts.Workout;
import com.cmpt276.project.porject.trackers.workouts.WorkoutApiService;
import com.cmpt276.project.porject.trackers.workouts.WorkoutRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

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

    @Autowired 
    private UserRepository userRepository;

    /** 
     * Helper for getting user session
    */
    private User getCurrentUser(HttpServletRequest request) {
        HttpSession session = request.getSession();
        return (User) session.getAttribute("session_user");
    }

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
    public String addFood(@RequestParam String foodDescription, HttpServletRequest request, Model model) {
        User user = getCurrentUser(request);
        Food food = foodApiService.getFoodNutrition(foodDescription);

        if (food == null) {
            model.addAttribute("messageType", "error");
            System.err.println("Failed to find nutrition info for: " + food);
        } else {
            model.addAttribute("food", food);
            
            if (user != null) {
                food.setUserId(user.getUid());
                foodRepository.save(food);
            }
        }

        // SENDS BACK TO FORM FOR NOW FOR TESTING
        return "add-food";
    }

    
    @PostMapping("/add-workout")
    public String addWorkout(@RequestParam String activity, int duration, HttpServletRequest request, Model model) {
        User user = getCurrentUser(request);
        Workout workout = workoutApiService.getWorkout(activity, duration);

        //Check valid obj
        if (workout == null) {
            model.addAttribute("messageType", "error");
            System.err.println("Failed to find calories burned for: " + activity);

        } else {
            model.addAttribute("workout", workout);

            if (user != null) {
                workout.setUserId(user.getUid());
                workoutRepository.save(workout);
            }
            
        }

        // SENDS BACK TO FORM FOR NOW FOR TESTING
        return "add-workout";
    }

}
