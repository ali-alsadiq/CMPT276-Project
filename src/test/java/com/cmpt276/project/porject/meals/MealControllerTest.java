package com.cmpt276.project.porject.meals;

import com.cmpt276.project.porject.auth.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.mockito.Mockito;
import static org.mockito.ArgumentMatchers.anyInt;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;

@WebMvcTest(MealController.class)
public class MealControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MealService mealService;

    @MockitoBean
    private FoodApiService foodApiService;

    @MockitoBean(name = "rankService")
    private com.cmpt276.project.porject.rank.RankService rankService;

    @Test
    public void testGetCalorieTrackerRedirectIfNotLoggedIn() throws Exception {
        mockMvc.perform(get("/calorie-tracker"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    public void testGetCalorieTrackerWithUser() throws Exception {
        Mockito.when(rankService.getTierName(anyInt())).thenReturn("Bronze");
        User mockUser = new User("Test", "User", "testuser", "password", "USER");
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("session_user", mockUser);

        mockMvc.perform(get("/calorie-tracker").session(session))
                .andExpect(status().isOk());
    }
}
