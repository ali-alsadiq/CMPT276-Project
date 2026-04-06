package com.cmpt276.project.porject.trackers;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.cmpt276.project.porject.auth.User;
import com.cmpt276.project.porject.rank.RewardService;
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
    private WorkoutRepository workoutRepository;

    @Autowired
    private RewardService rewardService;

    /**
     * Helper for getting user session
     */
    private User getCurrentUser(HttpServletRequest request) {
        HttpSession session = request.getSession();
        return (User) session.getAttribute("session_user");
    }

    private void populateWorkoutTrackerModel(HttpServletRequest request, Model model) {
        User user = getCurrentUser(request);
        List<Workout> workouts = new ArrayList<>();
        int weeklyBurnGoal = (int) user.getWeeklyCaloriesBurnedTarget();

        if (user != null) {
            workouts = workoutRepository.findByUserIdOrderByWorkoutDateDesc(user.getUid());
        }

        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.with(DayOfWeek.MONDAY);
        int[] dailyCalories = new int[7];
        int weeklyCalories = 0;

        for (Workout workout : workouts) {
            LocalDateTime workoutDate = workout.getWorkoutDate();
            if (workoutDate == null) {
                continue;
            }

            LocalDate workoutDay = workoutDate.toLocalDate();
            if (workoutDay.isBefore(weekStart) || workoutDay.isAfter(weekStart.plusDays(6))) {
                continue;
            }

            int dayIndex = workoutDay.getDayOfWeek().getValue() - 1;
            dailyCalories[dayIndex] += workout.getCalsBurned();
            weeklyCalories += workout.getCalsBurned();
        }

        int weeklyPercent = Math.min(100, (int) Math.round((weeklyCalories * 100.0) / weeklyBurnGoal));
        int maxDailyCalories = Math.max(1, (int) Math.max(weeklyBurnGoal / 7, findMax(dailyCalories)));

        List<String> dayLabels = List.of("M", "T", "W", "Th", "F", "Sa", "Su");
        List<String> dayColors = List.of("#962EFF", "#3A86FF", "#00F5FF", "#A8FF60", "#39FF14", "#FFC857", "#FF5C8A");
        List<WorkoutDaySummary> workoutWeek = new ArrayList<>();

        for (int i = 0; i < 7; i++) {
            int calories = dailyCalories[i];
            int percent = Math.min(100, (int) Math.round((calories * 100.0) / maxDailyCalories));
            workoutWeek.add(new WorkoutDaySummary(dayLabels.get(i), calories, percent, dayColors.get(i)));
        }

        model.addAttribute("workoutWeek", workoutWeek);
        model.addAttribute("weeklyWorkoutCalories", weeklyCalories);
        model.addAttribute("weeklyWorkoutPercent", weeklyPercent);
        model.addAttribute("weeklyWorkoutGoal", weeklyBurnGoal);
        model.addAttribute("recentWorkouts", workouts.stream().limit(5).toList());
    }

    private int findMax(int[] values) {
        int max = 0;
        for (int value : values) {
            if (value > max) {
                max = value;
            }
        }
        return max;
    }

    /**
     * workout test page get mapping
     * 
     * @return add-workout template view
     */
    @GetMapping("/add-workout")
    public String showAddWorkout(HttpServletRequest request, Model model) {
        populateWorkoutTrackerModel(request, model);
        return "add-workout";
    }

    /**
     * Post mapping to add-workout test page
     * 
     * @param activity the activity performed
     * @param duration the length in minutes of the activity
     * @param request  HttpServletRequest
     * @param model    model
     * @return returns add-workout test view with workout object
     */
    @PostMapping("/add-workout")
    public String addWorkout(@RequestParam String activity, int duration, HttpServletRequest request, Model model) {
        User user = getCurrentUser(request);
        Workout workout = workoutApiService.getWorkout(activity, duration);
        

        // Check valid obj
        if (workout == null) {
            model.addAttribute("messageType", "error");
            System.err.println("Failed to find calories burned for: " + activity);

        } else {
            model.addAttribute("workout", workout);

            // add user id if logged in
            if (user != null) {
                workout.setUserId(user.getUid());
                workoutRepository.save(workout);

                rewardService.rewardForWorkoutLog(user, workout.getWorkoutDate());
            }
        }

        populateWorkoutTrackerModel(request, model);

        // SENDS BACK TO FORM FOR NOW FOR TESTING
        return "add-workout";
    }

    public record WorkoutDaySummary(String label, int calories, int percent, String color) {

    }
}
