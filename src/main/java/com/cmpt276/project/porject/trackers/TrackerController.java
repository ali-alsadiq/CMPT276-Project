package com.cmpt276.project.porject.trackers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.cmpt276.project.porject.auth.User;
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

    /** 
     * Helper for getting user session
    */
    private User getCurrentUser(HttpServletRequest request) {
        HttpSession session = request.getSession();
        return (User) session.getAttribute("session_user");
    }

    /**
     * workout test page get mapping
     * @return add-workout template view
     */
    @GetMapping("/add-workout")
    public String showAddWorkout() {
        return "add-workout";
    }

     /**
     * food test page get mapping
     * @return add-food template view
     */
    @GetMapping("/add-food")
    public String showAddFood() {
        return "add-food";
    }

    /**
     * Post mapping to add-food test page
     * 
     * @param foodDescription natural description of users meal/food
     * @param request HttpServletRequest
     * @param model Model
     * @return returns add-food test view with list of foods
     */
    // @PostMapping("/add-food")
    // public String addFood(@RequestParam String foodDescription, HttpServletRequest request, Model model) {
    //     User user = getCurrentUser(request);
    //     List<Food> mealFoods = foodApiService.getMealNutrition(foodDescription);

    //     //check List isnt empty
    //     if (mealFoods.size() < 0 || mealFoods == null) {
    //         model.addAttribute("messageType", "error");
    //         System.err.println("Failed to find nutrition info for: " + mealFoods.get(0));
    //     } else {
    //         model.addAttribute("mealFoods", mealFoods);
    //         //add user id if logged in
    //         if (user != null) {
    //             foodRepository.save(mealFoods.get(0));
    //         }
    //     }

    //     // SENDS BACK TO FORM FOR NOW FOR TESTING
    //     return "add-food";
    // }

    /**
     *  Post mapping to add-workout test page
     * 
     * @param activity the activity performed
     * @param duration the length in minutes of the activity
     * @param request HttpServletRequest
     * @param model model
     * @return returns add-workout test view with workout object
     */
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

            //add user id if logged in
            if (user != null) {
                workout.setUserId(user.getUid());
                workoutRepository.save(workout);
            }
            
        }

        // SENDS BACK TO FORM FOR NOW FOR TESTING
        return "add-workout";
    }

}
