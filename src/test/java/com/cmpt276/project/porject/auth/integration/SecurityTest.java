package com.cmpt276.project.porject.auth.integration;

import com.cmpt276.project.porject.auth.User;
import com.cmpt276.project.porject.auth.UserController;
import com.cmpt276.project.porject.auth.UserRepository;

import org.junit.jupiter.api.Test;
import com.cmpt276.project.porject.rank.RankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.mock.web.MockHttpSession;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class SecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @org.springframework.test.context.bean.override.mockito.MockitoBean
    private com.cmpt276.project.porject.friends.FriendsRepository friendsRepository;

    @org.springframework.test.context.bean.override.mockito.MockitoBean
    private com.cmpt276.project.porject.trackers.workouts.WorkoutRepository workoutRepository;

    @org.springframework.test.context.bean.override.mockito.MockitoBean
    private com.cmpt276.project.porject.meals.MealRepository mealRepository;

    @org.springframework.test.context.bean.override.mockito.MockitoBean
    private com.cmpt276.project.porject.rank.RewardService rewardService;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean(name = "rankService")
    private RankService rankService;

    /* Tests that unauthorized access to protected pages redirects to login */
    @Test
    public void dashboard_noSession_redirectsToLogin() throws Exception {
        mockMvc.perform(get("/dashboard"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    public void adminDashboard_noSession_redirectsToLogin() throws Exception {
        mockMvc.perform(get("/admin-dashboard"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    public void profile_noSession_redirectsToLogin() throws Exception {
        mockMvc.perform(get("/profile"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    /* Tests that GET /register page loads for unauthenticated users */
    @Test
    public void getRegisterPage_noSession_returnsRegisterView() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("users/register"));
    }

    /* Tests that GET /logout invalidates session and redirects to login */
    @Test
    public void logout_invalidatesSessionAndRedirectsToLogin() throws Exception {
        User testUser = new User("User_test1", "TestLastname", "user123", "pass", "USER");
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("session_user", testUser);

        mockMvc.perform(get("/logout").session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    /* Tests that visiting login page while logged in redirects to dashboard */
    @Test
    public void login_activeSession_redirectsForUser() throws Exception {
        User testUser = new User("User_test1", "TestLastname", "user123", "pass", "USER");
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("session_user", testUser);

        mockMvc.perform(get("/login").session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dashboard"));
    }

    /* Tests that visiting register page while logged in redirects to dashboard */
    @Test
    public void register_activeSession_redirectsForUser() throws Exception {
        User testUser = new User("User_test1", "TestLastname", "user123", "pass", "USER");
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("session_user", testUser);

        mockMvc.perform(get("/register").session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dashboard"));
    }

    /*
     * Tests that visiting login page while logged in as admin redirects to
     * admin-dashboard
     */
    @Test
    public void login_activeSession_redirectsForAdmin() throws Exception {
        User testUser = new User("Admin_test1", "TestLastname", "admin123", "pass", "ADMIN");
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("session_user", testUser);

        mockMvc.perform(get("/login").session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin-dashboard"));
    }

    /*
     * Tests that visiting register page while logged in as admin redirects to
     * admin-dashboard
     */
    @Test
    public void register_activeSession_redirectsForAdmin() throws Exception {
        User testUser = new User("Admin_test1", "TestLastname", "admin123", "pass", "ADMIN");
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("session_user", testUser);

        mockMvc.perform(get("/register").session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin-dashboard"));
    }
}
