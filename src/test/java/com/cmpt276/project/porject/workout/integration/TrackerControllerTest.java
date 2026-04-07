package com.cmpt276.project.porject.workout.integration;

import com.cmpt276.project.porject.auth.User;
import com.cmpt276.project.porject.trackers.TrackerController;
import com.cmpt276.project.porject.trackers.workouts.Workout;
import com.cmpt276.project.porject.trackers.workouts.WorkoutApiService;
import com.cmpt276.project.porject.trackers.workouts.WorkoutRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TrackerController.class)
public class TrackerControllerTest {
        @Autowired
        private MockMvc mockMvc;

        @org.springframework.test.context.bean.override.mockito.MockitoBean
        private com.cmpt276.project.porject.friends.FriendsRepository friendsRepository;

        @org.springframework.test.context.bean.override.mockito.MockitoBean
        private com.cmpt276.project.porject.rank.RewardService rewardService;

        @MockitoBean
        private WorkoutApiService workoutApiService;

        @MockitoBean
        private WorkoutRepository workoutRepository;

        @MockitoBean(name = "rankService")
        private com.cmpt276.project.porject.rank.RankService rankService;

        private User mockUser;
        private MockHttpSession session;

        @BeforeEach
        public void setup() {
                mockUser = new User("User_test1", "TestLastname", "user_test1", "pass123", "USER");
                mockUser.setWeeklyCaloriesBurnedTarget(500);

                session = new MockHttpSession();
                session.setAttribute("session_user", mockUser);

                Mockito.when(workoutRepository.findByUserIdOrderByWorkoutDateDesc(anyInt()))
                                .thenReturn(Collections.emptyList());
        }

        // GET /add-workout returns the add-workout view if session is valid
        @Test
        public void addWorkout_withSession_returnsView() throws Exception {
                mockMvc.perform(get("/add-workout").session(session))
                                .andExpect(status().isOk())
                                .andExpect(view().name("add-workout"));
        }

        // GET /add-workout returns workoutWeek
        @Test
        public void addWorkout_withSession_modelContainsWorkoutWeek() throws Exception {
                mockMvc.perform(get("/add-workout").session(session))
                                .andExpect(status().isOk())
                                .andExpect(model().attributeExists("workoutWeek"));
        }

        // GET /add-workout returns weeklyWorkoutCalories
        @Test
        public void addWorkout_withSession_modelContainsWeeklyCalories() throws Exception {
                mockMvc.perform(get("/add-workout").session(session))
                                .andExpect(status().isOk())
                                .andExpect(model().attributeExists("weeklyWorkoutCalories"));
        }

        // GET /add-workout returns weeklyWorkoutPercent
        @Test
        public void addWorkout_withSession_modelContainsWeeklyPercent() throws Exception {
                mockMvc.perform(get("/add-workout").session(session))
                                .andExpect(status().isOk())
                                .andExpect(model().attributeExists("weeklyWorkoutPercent"));
        }

        // GET /add-workout returns recentWorkouts
        @Test
        public void addWorkout_withSession_modelContainsRecentWorkouts() throws Exception {
                mockMvc.perform(get("/add-workout").session(session))
                                .andExpect(status().isOk())
                                .andExpect(model().attributeExists("recentWorkouts"));
        }

        // GET /add-workout weekly percent is 0 when user has no workouts
        @Test
        public void addWorkout_weeklyPercentIsZeroWithNoWorkouts() throws Exception {
                mockMvc.perform(get("/add-workout").session(session))
                                .andExpect(status().isOk())
                                .andExpect(model().attribute("weeklyWorkoutPercent", 0))
                                .andExpect(model().attribute("weeklyWorkoutCalories", 0));
        }

        // GET /add-workout weekly percent is capped at 100 when calories exceed goal
        @Test
        public void addWorkout_weeklyPercentCapAt100() throws Exception {
                LocalDateTime thisWeek = currentMonday().atTime(9, 0);
                Workout massiveWorkout = new Workout("Running", 999, 9999, thisWeek);

                Mockito.when(workoutRepository.findByUserIdOrderByWorkoutDateDesc(anyInt()))
                                .thenReturn(List.of(massiveWorkout));

                mockMvc.perform(get("/add-workout").session(session))
                                .andExpect(status().isOk())
                                .andExpect(model().attribute("weeklyWorkoutPercent", 100));
        }

        // GET /add-workout workouts inside the current week are counted in daily bars
        @Test
        public void addWorkout_workoutsThisWeek_countedInWeeklyCalories() throws Exception {
                LocalDateTime thisWeek = currentMonday().atTime(9, 0);
                Workout workout = new Workout("Cycling", 30, 250, thisWeek);

                Mockito.when(workoutRepository.findByUserIdOrderByWorkoutDateDesc(anyInt()))
                                .thenReturn(List.of(workout));

                mockMvc.perform(get("/add-workout").session(session))
                                .andExpect(status().isOk())
                                .andExpect(model().attribute("weeklyWorkoutCalories", 250));
        }

        // GET /add-workout workouts outside the current week do not contribute to the
        // weekly
        @Test
        public void addWorkout_workoutsOutsideWeek_notCountedInBars() throws Exception {
                LocalDateTime lastWeek = currentMonday().minusDays(7).atTime(9, 0);
                Workout oldWorkout = new Workout("Swimming", 45, 400, lastWeek);

                Mockito.when(workoutRepository.findByUserIdOrderByWorkoutDateDesc(anyInt()))
                                .thenReturn(List.of(oldWorkout));

                mockMvc.perform(get("/add-workout").session(session))
                                .andExpect(status().isOk())
                                .andExpect(model().attribute("weeklyWorkoutCalories", 0));
        }

        // GET /add-workout recentWorkouts is limited to 5 even if more workouts exist
        @Test
        public void addWorkout_recentWorkoutsLimitedToFive() throws Exception {
                LocalDateTime thisWeek = currentMonday().atTime(9, 0);

                // Init 8 workouts
                List<Workout> workouts = new ArrayList<>();
                for (int i = 0; i < 8; i++) {
                        workouts.add(new Workout("Running", 30, 200, thisWeek));
                }

                Mockito.when(workoutRepository.findByUserIdOrderByWorkoutDateDesc(anyInt()))
                                .thenReturn(workouts);

                mockMvc.perform(get("/add-workout").session(session))
                                .andExpect(status().isOk())
                                .andExpect(model().<List<?>>attribute("recentWorkouts",
                                                org.hamcrest.Matchers.hasSize(5)));
        }

        // POST /add-workout saves workout and adds it to model on valid API response
        @Test
        public void addWorkout_validActivity_savesWorkoutAndShowsCard() throws Exception {
                LocalDateTime thisWeek = currentMonday().atTime(9, 0);
                Workout workout = new Workout("Running", 30, 300, thisWeek);
                Mockito.when(workoutApiService.getWorkout(anyString(), anyInt())).thenReturn(workout);

                mockMvc.perform(post("/add-workout").session(session)
                                .param("activity", "Running")
                                .param("duration", "30"))
                                .andExpect(status().isOk())
                                .andExpect(model().attributeExists("workout"));

                Mockito.verify(workoutRepository, Mockito.times(1)).save(Mockito.any(Workout.class));
        }

        // POST /add-workout shows error message when API returns null
        @Test
        public void addWorkout_apiReturnsNull_showsErrorMessage() throws Exception {
                Mockito.when(workoutApiService.getWorkout(anyString(), anyInt())).thenReturn(null);

                mockMvc.perform(post("/add-workout").session(session)
                                .param("activity", "unknownactivity")
                                .param("duration", "30"))
                                .andExpect(status().isOk())
                                .andExpect(model().attribute("messageType", "error"));

                Mockito.verify(workoutRepository, Mockito.never()).save(Mockito.any(Workout.class));
        }

        // POST /add-workout sets user ID on the workout before saving
        @Test
        public void addWorkout_validActivity_userIdSetOnWorkout() throws Exception {
                LocalDateTime thisWeek = currentMonday().atTime(9, 0);
                Workout workout = new Workout("Cycling", 45, 400, thisWeek);

                Mockito.when(workoutApiService.getWorkout(anyString(), anyInt())).thenReturn(workout);

                mockMvc.perform(post("/add-workout").session(session)
                                .param("activity", "Cycling")
                                .param("duration", "45"))
                                .andExpect(status().isOk());

                Mockito.verify(workoutRepository)
                                .save(Mockito.argThat(saved -> saved.getUserId() == mockUser.getUid()));
        }

        // Helper
        private LocalDate currentMonday() {
                return LocalDate.now().with(DayOfWeek.MONDAY);
        }
}
