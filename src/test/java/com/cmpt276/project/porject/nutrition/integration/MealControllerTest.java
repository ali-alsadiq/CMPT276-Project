package com.cmpt276.project.porject.nutrition.integration;

import com.cmpt276.project.porject.auth.User;
import com.cmpt276.project.porject.meals.Food;
import com.cmpt276.project.porject.meals.FoodApiService;
import com.cmpt276.project.porject.meals.MealController;
import com.cmpt276.project.porject.meals.MealService;
import com.cmpt276.project.porject.rank.RankService;
import com.cmpt276.project.porject.rank.RewardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MealController.class)
public class MealControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private MealService mealService;

        @MockitoBean
        private FoodApiService foodApiService;

        @MockitoBean(name = "rankService")
        private RankService rankService;

        @MockitoBean
        private RewardService rewardService;

        private User mockUser;
        private MockHttpSession session;

        @BeforeEach
        public void setup() {
                mockUser = new User("User_test1", "TestLastname", "user_test1", "pass123", "USER");

                mockUser.setWeeklyCaloriesConsumedTarget(2000);
                mockUser.setWeeklyProtienTarget(500);
                mockUser.setWeeklyCarbsTarget(2000);
                mockUser.setWeeklyFatsTarget(500);
                mockUser.setWeeklyFibreTarget(200);

                session = new MockHttpSession();
                session.setAttribute("session_user", mockUser);

                Mockito.when(mealService.getUserMeals(anyInt())).thenReturn(Collections.emptyList());
                Mockito.when(mealService.getTodayTotals(anyInt())).thenReturn(Map.of(
                                "calories", 0.0, "protein", 0.0, "carbs", 0.0,
                                "fats", 0.0, "fiber", 0.0));
        }

        // GET /calorie-tracker redirect to login if invalid session
        @Test
        public void calorieTracker_noSession_redirectsToLogin() throws Exception {
                mockMvc.perform(get("/calorie-tracker"))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/login"));
        }

        // GET /calorie-tracker returns calorie-tracker page if session is valid
        @Test
        public void calorieTracker_withSession_returnsView() throws Exception {
                mockMvc.perform(get("/calorie-tracker").session(session))
                                .andExpect(status().isOk())
                                .andExpect(view().name("calorie-tracker/calorie-tracker"));
        }

        // GET /calorie-tracker returns meals if session is valid
        @Test
        public void calorieTracker_withSession_modelContainsMeals() throws Exception {
                mockMvc.perform(get("/calorie-tracker").session(session))
                                .andExpect(status().isOk())
                                .andExpect(model().attributeExists("meals"));
        }

        // GET /calorie-tracker returns today totals if session is valid
        @Test
        public void calorieTracker_withSession_modelContainsTodayTotals() throws Exception {
                mockMvc.perform(get("/calorie-tracker").session(session))
                                .andExpect(status().isOk())
                                .andExpect(model().attributeExists("todayTotals"));
        }

        // GET /calorie-tracker returns progress circle model attributes if session is
        // valid
        @Test
        public void calorieTracker_userWithTargets_percentagesAreCorrect() throws Exception {
                Mockito.when(mealService.getTodayTotals(anyInt())).thenReturn(Map.of(
                                "calories", 1000.0, "protein", 250.0, "carbs", 1000.0,
                                "fats", 250.0, "fiber", 100.0));

                mockMvc.perform(get("/calorie-tracker").session(session))
                                .andExpect(status().isOk())
                                .andExpect(model().attribute("totalPercent", 50))
                                .andExpect(model().attribute("proteinPercent", 50))
                                .andExpect(model().attribute("carbsPercent", 50))
                                .andExpect(model().attribute("fatsPercent", 50))
                                .andExpect(model().attribute("fibrePercent", 50));
        }

        // GET /calorie-tracker progress circle defaults to 2000 when targets are 0
        @Test
        public void calorieTracker_userWithZeroTargets_usesDefaultGoal() throws Exception {
                User zeroTargetUser = new User("User_test2", "TestLastname", "user_test2", "pass123", "USER");

                zeroTargetUser.setWeeklyCaloriesConsumedTarget(0);
                zeroTargetUser.setWeeklyProtienTarget(0);
                zeroTargetUser.setWeeklyCarbsTarget(0);
                zeroTargetUser.setWeeklyFatsTarget(0);
                zeroTargetUser.setWeeklyFibreTarget(0);

                MockHttpSession zeroSession = new MockHttpSession();
                zeroSession.setAttribute("session_user", zeroTargetUser);

                Mockito.when(mealService.getTodayTotals(anyInt())).thenReturn(Map.of(
                                "calories", 1000.0, "protein", 1000.0, "carbs", 1000.0,
                                "fats", 1000.0, "fiber", 1000.0));

                mockMvc.perform(get("/calorie-tracker").session(zeroSession))
                                .andExpect(status().isOk())
                                .andExpect(model().attribute("totalGoal", 2000))
                                .andExpect(model().attribute("totalPercent", 50));
        }

        // GET /calorie-tracker progress circle does not exceed 100%
        @Test
        public void calorieTracker_percentagesCapAt100() throws Exception {
                Mockito.when(mealService.getTodayTotals(anyInt())).thenReturn(Map.of(
                                "calories", 6000.0, "protein", 1500.0, "carbs", 6000.0,
                                "fats", 1500.0, "fiber", 600.0));

                mockMvc.perform(get("/calorie-tracker").session(session))
                                .andExpect(status().isOk())
                                .andExpect(model().attribute("totalPercent", 100))
                                .andExpect(model().attribute("proteinPercent", 100));
        }

        // GET /calorie-tracker progress circle is 0 when no meals exist
        @Test
        public void calorieTracker_percentageIsZeroWhenNoMeals() throws Exception {
                mockMvc.perform(get("/calorie-tracker").session(session))
                                .andExpect(status().isOk())
                                .andExpect(model().attribute("totalPercent", 0))
                                .andExpect(model().attribute("proteinPercent", 0))
                                .andExpect(model().attribute("carbsPercent", 0))
                                .andExpect(model().attribute("fatsPercent", 0))
                                .andExpect(model().attribute("fibrePercent", 0));
        }

        // POST /calorie-tracker/search returns empty list if session is invalid
        @Test
        public void search_noSession_returnsEmptyList() throws Exception {
                mockMvc.perform(post("/calorie-tracker/search")
                                .param("foodDescription", "apple"))
                                .andExpect(status().isOk())
                                .andExpect(content().json("[]"));
        }

        // POST /calorie-tracker/search returns empty list if description is blank
        @Test
        public void search_emptyDescription_returnsEmptyList() throws Exception {
                mockMvc.perform(post("/calorie-tracker/search").session(session)
                                .param("foodDescription", "   "))
                                .andExpect(status().isOk())
                                .andExpect(content().json("[]"));
        }

        // POST /calorie-tracker/search returns food list if valid description
        @Test
        public void search_validDescription_returnsFoodList() throws Exception {
                Food food = new Food("apple", 100.0, 95.0, 0.5, 25.0, 0.3, 4.0, 19.0, 2.0, 107.0, 0.0);
                Mockito.when(foodApiService.getMealNutrition("apple")).thenReturn(List.of(food));

                mockMvc.perform(post("/calorie-tracker/search").session(session)
                                .param("foodDescription", "apple"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$[0].foodName").value("apple"));
        }

        // POST /calorie-tracker/search returns empty list if API returns null
        @Test
        public void search_apiReturnsNull_returnsEmptyList() throws Exception {
                Mockito.when(foodApiService.getMealNutrition(Mockito.anyString())).thenReturn(null);

                mockMvc.perform(post("/calorie-tracker/search").session(session)
                                .param("foodDescription", "unknownfood"))
                                .andExpect(status().isOk())
                                .andExpect(content().json("[]"));
        }

        // POST /calorie-tracker/search returns empty list if total calories > 10000
        @Test
        public void search_totalCaloriesOver10000_returnsEmptyList() throws Exception {
                Food massiveFood = new Food("giant", 500.0, 15000.0, 10.0, 10.0, 10.0, 1.0, 1.0, 1.0, 1.0, 1.0);
                Mockito.when(foodApiService.getMealNutrition(Mockito.anyString())).thenReturn(List.of(massiveFood));

                mockMvc.perform(post("/calorie-tracker/search").session(session)
                                .param("foodDescription", "giant meal"))
                                .andExpect(status().isOk())
                                .andExpect(content().json("[]"));
        }

        // POST /calorie-tracker/search strips decimals in description before searching
        @Test
        public void search_decimalInDescription_stripsBeforeSearch() throws Exception {
                Mockito.when(foodApiService.getMealNutrition(Mockito.anyString())).thenReturn(Collections.emptyList());

                mockMvc.perform(post("/calorie-tracker/search").session(session)
                                .param("foodDescription", "1.5 chicken"))
                                .andExpect(status().isOk())
                                .andExpect(content().json("[]"));
        }

        // POST /meals/add redirect to login if no session is invalid
        @Test
        public void addMeal_noSession_redirectsToLogin() throws Exception {
                mockMvc.perform(post("/meals/add")
                                .param("mealType", "Lunch")
                                .param("consumedDate", LocalDateTime.now().toString())
                                .param("foodOrder", "apple"))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/login"));
        }

        // POST /meals/add redirect to calorie-tracker if date is invalid
        @Test
        public void addMeal_invalidDate_redirectsWithError() throws Exception {
                mockMvc.perform(post("/meals/add").session(session)
                                .param("mealType", "Lunch")
                                .param("consumedDate", "not-a-date")
                                .param("foodOrder", "apple")
                                .param("requestedServSizes[apple]", "100"))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/calorie-tracker"));
        }

        // POST /meals/add redirect to calorie-tracker if serving size is missing
        @Test
        public void addMeal_missingSizing_redirectsWithError() throws Exception {
                mockMvc.perform(post("/meals/add").session(session)
                                .param("mealType", "Lunch")
                                .param("consumedDate", LocalDateTime.now().toString())
                                .param("foodOrder", "apple"))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/calorie-tracker"));
        }

        // POST /meals/add redirect to calorie-tracker if serving size is 0 or negative
        @Test
        public void addMeal_servingSizeZeroOrNegative_redirectsWithError() throws Exception {
                mockMvc.perform(post("/meals/add").session(session)
                                .param("mealType", "Lunch")
                                .param("consumedDate", LocalDateTime.now().toString())
                                .param("foodOrder", "apple")
                                .param("requestedServSizes[apple]", "0"))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/calorie-tracker"));
        }

        // POST /meals/add saves meal and redirects to calorie-tracker on valid
        // submission
        @Test
        public void addMeal_validSubmission_savesMealAndRedirects() throws Exception {
                Food food = new Food("apple", 100.0, 95.0, 0.5, 25.0, 0.3, 4.0, 19.0, 2.0, 107.0, 0.0);

                Mockito.when(foodApiService.getMealNutrition(Mockito.anyString())).thenReturn(List.of(food));

                mockMvc.perform(post("/meals/add").session(session)
                                .param("mealType", "Lunch")
                                .param("mealName", "My Lunch")
                                .param("consumedDate", LocalDateTime.now().toString())
                                .param("foodOrder", "apple")
                                .param("requestedServSizes[apple]", "100"))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/calorie-tracker"));

                Mockito.verify(mealService, Mockito.times(1))
                                .addMeal(Mockito.any(), Mockito.anyString(), Mockito.anyString(),
                                                Mockito.any(LocalDateTime.class), Mockito.anyList());
        }

        // POST /meals/add redirects with error if API fails
        @Test
        public void addMeal_apiFails_redirectsWithError() throws Exception {
                Mockito.when(foodApiService.getMealNutrition(Mockito.anyString())).thenReturn(null);

                mockMvc.perform(post("/meals/add").session(session)
                                .param("mealType", "Lunch")
                                .param("consumedDate", LocalDateTime.now().toString())
                                .param("foodOrder", "apple")
                                .param("requestedServSizes[apple]", "100"))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/calorie-tracker"));

                Mockito.verify(mealService, Mockito.never())
                                .addMeal(Mockito.any(), Mockito.anyString(), Mockito.anyString(),
                                                Mockito.any(LocalDateTime.class), Mockito.anyList());
        }

        // POST /meals/add redirects with error if total calories > 10000
        @Test
        public void addMeal_totalCaloriesOver10000_redirectsWithError() throws Exception {
                Food massiveFood = new Food("giant", 500.0, 15000.0, 10.0, 10.0, 10.0, 1.0, 1.0, 1.0, 1.0, 1.0);
                Mockito.when(foodApiService.getMealNutrition(Mockito.anyString())).thenReturn(List.of(massiveFood));

                mockMvc.perform(post("/meals/add").session(session)
                                .param("mealType", "Lunch")
                                .param("consumedDate", LocalDateTime.now().toString())
                                .param("foodOrder", "giant")
                                .param("requestedServSizes[giant]", "500"))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/calorie-tracker"));

                Mockito.verify(mealService, Mockito.never())
                                .addMeal(Mockito.any(), Mockito.anyString(), Mockito.anyString(),
                                                Mockito.any(LocalDateTime.class), Mockito.anyList());
        }
}
