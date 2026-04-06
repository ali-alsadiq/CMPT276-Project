package com.cmpt276.project.porject.auth.integration;

import com.cmpt276.project.porject.auth.User;
import com.cmpt276.project.porject.auth.UserController;
import com.cmpt276.project.porject.auth.UserRepository;
import com.cmpt276.project.porject.rank.RankService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class OnboardingTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean(name = "rankService")
    private RankService rankService;

    private Map<String, String> defaultParams;
    private User mockUser;

    @BeforeEach
    public void setup() {
        mockUser = new User("User_test1", "TestLastname", "user123", "pass123", "USER");
        mockUser.setUserSetTargets(false);

        defaultParams = new HashMap<>();
        defaultParams.put("sex", "Male");
        defaultParams.put("dateOfBirth", "2000-01-01");
        defaultParams.put("height", "175");
        defaultParams.put("weeklyCaloriesBurnedTarget", "2500");
        defaultParams.put("weeklyCaloriesConsumedTarget", "2000");
        defaultParams.put("weeklyProtienTarget", "500");
        defaultParams.put("weeklyCarbsTarget", "2000");
        defaultParams.put("weight", "72");
        defaultParams.put("weeklyFatsTarget", "500");
        defaultParams.put("weeklyFibreTarget", "200");
    }

    private MockHttpSession getAuthenticatedSession(User user) {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("session_user", user);
        return session;
    }

    // Redirection & Authentication

    @Test
    public void onBoarding_unauthenticated_redirectsToLogin() throws Exception {
        mockMvc.perform(get("/onBoarding"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    public void onBoarding_postUnauthenticated_redirectsToLogin() throws Exception {
        mockMvc.perform(post("/onBoarding"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    public void onBoarding_getWhenAlreadyOnboarded_redirectsToDashboard() throws Exception {
        mockUser.setUserSetTargets(true);

        mockMvc.perform(get("/onBoarding").session(getAuthenticatedSession(mockUser)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dashboard"));
    }

    @Test
    public void onBoarding_postWhenAlreadyOnboarded_redirectsToDashboard() throws Exception {
        mockUser.setUserSetTargets(true);

        mockMvc.perform(post("/onBoarding").session(getAuthenticatedSession(mockUser)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dashboard"));
    }

    // Persistence

    @Test
    public void onBoarding_validSubmission_setsUserSetTargetsTrue() throws Exception {
        Mockito.when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        var requestBuilder = post("/onBoarding").session(getAuthenticatedSession(mockUser));
        defaultParams.forEach(requestBuilder::param);

        mockMvc.perform(requestBuilder).andExpect(status().is3xxRedirection());

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        Mockito.verify(userRepository, Mockito.atLeastOnce()).save(userCaptor.capture());
        assertTrue(userCaptor.getValue().getUserSetTargets());
    }

    @Test
    public void onBoarding_validSubmission_persistsAllProfileFields() throws Exception {
        Mockito.when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        var requestBuilder = post("/onBoarding").session(getAuthenticatedSession(mockUser));
        defaultParams.forEach(requestBuilder::param);

        mockMvc.perform(requestBuilder).andExpect(status().is3xxRedirection());

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        Mockito.verify(userRepository, Mockito.atLeastOnce()).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();

        assertEquals("Male", savedUser.getSex());
        assertEquals("2000-01-01", savedUser.getDateOfBirth().toString());
        assertEquals(175.0, savedUser.getHeight());
        assertEquals(72.0, savedUser.getWeight());
        assertEquals(2500.0, savedUser.getWeeklyCaloriesBurnedTarget());
        assertEquals(2000.0, savedUser.getWeeklyCaloriesConsumedTarget());
        assertEquals(500.0, savedUser.getDailyProtienTarget());
        assertEquals(2000.0, savedUser.getDailyCarbsTarget());
        assertEquals(500.0, savedUser.getDailyFatsTarget());
        assertEquals(200.0, savedUser.getDailyFibreTarget());
    }
}
