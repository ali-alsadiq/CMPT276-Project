package com.cmpt276.project.porject.trackers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.cmpt276.project.porject.trackers.workouts.WorkoutApiService;

@Controller
public class TrackerController {
    @Autowired
    private WorkoutApiService workoutApiService;

    @GetMapping("/test-workout")
        public String showTestPage() {
            return "test-workout";
        }

    @PostMapping("/test-workout")
    public String testWorkout(@RequestParam String activity,
                             @RequestParam int duration,
                             Model model) {
        int calories = workoutApiService.getCaloriesBurned(activity, duration);
        model.addAttribute("activity", activity);
        model.addAttribute("duration", duration);
        model.addAttribute("calories", calories);
        return "test-workout";
    }
}
