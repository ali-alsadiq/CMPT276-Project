package com.cmpt276.project.porject.trackers;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
    private WorkoutRepository workoutRepository;

    //Add workout page
    @GetMapping("/add-workout")
        public String showAddWorkout() {
            return "add-workout";
        }

    @GetMapping("/add-food")
        public String showAddFood() {
            return "add-food";
        }


    //SENDS BACK TO FORM FOR NOW FOR TESTING
    @PostMapping("/add-workout")
    public String addWorkout(@RequestParam String activity, @RequestParam int duration, Model model) {
        int calories = workoutApiService.getCaloriesBurned(activity, duration);
        Workout workout = new Workout(activity, duration, calories, LocalDateTime.now());
        model.addAttribute("workout", workout);

        workoutRepository.save(workout);

        return "add-workout";
    }
}
