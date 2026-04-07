package com.cmpt276.project.porject.trackers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import com.cmpt276.project.porject.auth.User;
import com.cmpt276.project.porject.rank.RewardService;
import com.cmpt276.project.porject.trackers.workouts.Workout;
import com.cmpt276.project.porject.trackers.workouts.WorkoutApiService;
import com.cmpt276.project.porject.trackers.workouts.WorkoutRepository;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TrackerController.class)
public class TrackerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private WorkoutApiService workoutApiService;

    @MockitoBean
    private WorkoutRepository workoutRepository;

    @MockitoBean
    private RewardService rewardService;

    @MockitoBean(name = "rankService")
    private com.cmpt276.project.porject.rank.RankService rankService;

    @Test
    public void testGetAddWorkoutPageWithUser() throws Exception {
        Mockito.when(rankService.getTierName(anyInt())).thenReturn("Bronze");
        User mockUser = new User("Test", "User", "testuser", "password", "USER");

        MockHttpSession session = new MockHttpSession();

        session.setAttribute("session_user", mockUser);

        mockMvc.perform(get("/add-workout").session(session))
                .andExpect(status().isOk());
    }

    @Test
    public void testWorkoutPageAccumulatesWeeklyCaloriesIncludingSunday() throws Exception {
        Mockito.when(rankService.getTierName(anyInt())).thenReturn("Bronze");

        User mockUser = new User("Test", "User", "testuser", "password", "USER");
        mockUser.setUid(42);
        mockUser.setWeeklyCaloriesBurnedTarget(1000);

        LocalDate sunday = LocalDate.now().with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.SUNDAY));
        Workout sundayWorkout = new Workout("Run", 30, 200, sunday.atTime(10, 0));
        sundayWorkout.setUserId(42);

        Workout mondayWorkout = new Workout("Cycle", 40, 300, sunday.plusDays(1).atTime(12, 0));
        mondayWorkout.setUserId(42);

        Mockito.when(workoutRepository.findByUserIdOrderByWorkoutDateDesc(eq(42)))
                .thenReturn(List.of(mondayWorkout, sundayWorkout));

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("session_user", mockUser);

        mockMvc.perform(get("/add-workout").session(session))
                .andExpect(status().isOk())
                .andExpect(model().attribute("weeklyWorkoutCalories", 500))
                .andExpect(model().attribute("weeklyWorkoutPercent", 50));
    }
}
