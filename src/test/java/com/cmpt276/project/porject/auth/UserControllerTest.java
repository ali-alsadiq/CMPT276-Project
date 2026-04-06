package com.cmpt276.project.porject.auth;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import com.cmpt276.project.porject.rank.RankService;
import com.cmpt276.project.porject.rank.RewardService;
import com.cmpt276.project.porject.trackers.workouts.WorkoutApiService;
import com.cmpt276.project.porject.trackers.workouts.WorkoutRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private UserRepository userRepository;

        @MockitoBean(name = "rankService")
        private RankService rankService;

        @MockitoBean
        private RewardService rewardService;

        @MockitoBean
        private WorkoutApiService workoutApiService;

        @MockitoBean
        private WorkoutRepository workoutRepository;

        /*
         * Tests login page is displayed when the user visits the login page.
         */
        @Test
        public void testGetLoginPage() throws Exception {
                mockMvc.perform(get("/login"))
                                .andExpect(status().isOk())
                                .andExpect(view().name("users/login"));
        }

        @Test
        void testLoginSuccessForAdmin() throws Exception {
                User mockUser = new User("Test", "Admin", "testadmin1", "StrongPass1!", "ADMIN");
                List<User> mockList = Collections.singletonList(mockUser);

                Mockito.when(userRepository.findByUsernameAndPassword("testadmin1", "StrongPass1!"))
                                .thenReturn(mockList);

                mockMvc.perform(post("/login")
                                .param("firstname", "Test")
                                .param("lastname", "Admin")
                                .param("username", "testadmin1")
                                .param("password", "StrongPass1!"))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/admin-dashboard"))
                                .andExpect(request().sessionAttribute("session_user", mockUser));
        }

        @Test
        public void testLoginFailureForUser() throws Exception {
                Mockito.when(userRepository.findByUsernameAndPassword("testuser2", "wrongpw"))
                                .thenReturn(Collections.emptyList());

                mockMvc.perform(post("/login")
                                .param("firstname", "Test")
                                .param("lastname", "User")
                                .param("username", "testuser2")
                                .param("password", "wrongpw"))
                                .andExpect(status().isOk())
                                .andExpect(view().name("users/login"))
                                .andExpect(model().attributeExists("error"))
                                .andExpect(request().sessionAttributeDoesNotExist("session_user"));
        }

        @Test
        public void testLoginFailureForAdmin() throws Exception {
                Mockito.when(userRepository.findByUsernameAndPassword("testadmin2", "wrongpw"))
                                .thenReturn(Collections.emptyList());

                mockMvc.perform(post("/login")
                                .param("firstname", "Test")
                                .param("lastname", "Admin")
                                .param("username", "testadmin2")
                                .param("password", "wrongpw"))
                                .andExpect(status().isOk())
                                .andExpect(view().name("users/login"))
                                .andExpect(model().attributeExists("error"))
                                .andExpect(request().sessionAttributeDoesNotExist("session_user"));
        }

        @Test
        public void testRegisterSuccess() throws Exception {
                mockMvc.perform(post("/register")
                                .param("firstname", "Test")
                                .param("lastname", "Admin")
                                .param("username", "newAdmin")
                                .param("password", "StrongPass1!")
                                .param("role", "ADMIN"))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/login"));

                Mockito.verify(userRepository, Mockito.times(1)).save(any(User.class));
        }

        @Test
        public void testRegisterWeakPassword() throws Exception {
                mockMvc.perform(post("/register")
                                .param("firstname", "Test")
                                .param("lastname", "User")
                                .param("username", "newUser")
                                .param("password", "weakpass"))
                                .andExpect(status().isOk())
                                .andExpect(view().name("users/register"))
                                .andExpect(model().attributeExists("passwordError"));

                Mockito.verify(userRepository, Mockito.never()).save(any(User.class));
        }

        /**
         * Tests that a user cannot access the admin dashboard.
         */
        @Test
        public void testAdminDashboardAccessDeniedForUser() throws Exception {
                User standardUser = new User("Test", "User", "normie", "pass123", "USER");

                MockHttpSession session = new MockHttpSession();
                session.setAttribute("session_user", standardUser);

                mockMvc.perform(get("/admin-dashboard").session(session))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/login"));
        }

        /*
         * Tests that profile page redirects if user is not logged in.
         */
        @Test
        public void testGetProfilePageRedirectIfNotLoggedIn() throws Exception {
                mockMvc.perform(get("/profile"))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/login"));
        }

        /*
         * Tests that profile page loads for logged-in user.
         */
        @Test
        public void testGetProfilePageSuccess() throws Exception {
                User mockUser = new User("Test", "User", "testuser1", "StrongPass1!", "USER");

                MockHttpSession session = new MockHttpSession();
                session.setAttribute("session_user", mockUser);

                mockMvc.perform(get("/profile").session(session))
                                .andExpect(status().isOk())
                                .andExpect(view().name("users/profile"))
                                .andExpect(model().attributeExists("user"))
                                .andExpect(model().attribute("user", mockUser));
        }

        /*
         * Tests successful profile update.
         */
        @Test
        public void testUpdateProfileSuccess() throws Exception {
                User mockUser = new User("Old", "Name", "testuser1", "StrongPass1!", "USER");

                MockHttpSession session = new MockHttpSession();
                session.setAttribute("session_user", mockUser);

                Mockito.when(userRepository.save(any(User.class)))
                                .thenAnswer(invocation -> invocation.getArgument(0));

                mockMvc.perform(post("/profile")
                                .session(session)
                                .param("firstname", "Jane")
                                .param("lastname", "Doe")
                                .param("sex", "Male")
                                .param("dateOfBirth", "2000-01-01")
                                .param("height", "175.5")
                                .param("weight", "72.3")
                                .param("weeklyWorkoutGoalCount", "3")
                                .param("weeklyCaloriesBurnedTarget", "500")
                                .param("weeklyCaloriesConsumedTarget", "14000")
                                .param("dailyProtienTarget", "150")
                                .param("dailyCarbsTarget", "250")
                                .param("dailyFatsTarget", "70")
                                .param("dailyFibreTarget", "30"))
                                .andExpect(status().isOk())
                                .andExpect(view().name("users/profile"));

                Mockito.verify(userRepository, Mockito.atLeastOnce()).save(any(User.class));
        }

        /*
         * Tests profile update fails with invalid data.
         */
        @Test
        public void testUpdateProfileFailsWithInvalidData() throws Exception {
                User mockUser = new User("Old", "Name", "testuser1", "StrongPass1!", "USER");

                MockHttpSession session = new MockHttpSession();
                session.setAttribute("session_user", mockUser);

                mockMvc.perform(post("/profile")
                                .session(session)
                                .param("firstname", "")
                                .param("lastname", "Jane")
                                .param("sex", "")
                                .param("dateOfBirth", "3000-01-01")
                                .param("height", "20")
                                .param("weight", "900")
                                .param("weeklyCaloriesBurnedTarget", "100")
                                .param("weeklyCaloriesConsumedTarget", "100")
                                .param("dailyProtienTarget", "100")
                                .param("dailyCarbsTarget", "100")
                                .param("dailyFatsTarget", "100")
                                .param("dailyFibreTarget", "100"))
                                .andExpect(status().isOk())
                                .andExpect(view().name("users/profile"))
                                .andExpect(model().attributeExists("firstnameError"))
                                .andExpect(model().attributeExists("sexError"))
                                .andExpect(model().attributeExists("dateOfBirthError"))
                                .andExpect(model().attributeExists("heightError"))
                                .andExpect(model().attributeExists("weightError"));

                Mockito.verify(userRepository, Mockito.never()).save(any(User.class));
        }

        /*
         * Tests profile update redirects if not logged in.
         */
        @Test
        public void testUpdateProfileRedirectIfNotLoggedIn() throws Exception {
                mockMvc.perform(post("/profile")
                                .param("firstname", "Ali")
                                .param("lastname", "Alsadiq")
                                .param("sex", "Male")
                                .param("dateOfBirth", "2000-01-01")
                                .param("height", "175")
                                .param("weight", "72")
                                .param("weeklyCaloriesBurnedTarget", "2500")
                                .param("weeklyCaloriesConsumedTarget", "2000")
                                .param("weeklyProtienTarget", "500")
                                .param("weeklyCarbsTarget", "800")
                                .param("weeklyFatsTarget", "300")
                                .param("weeklyFibreTarget", "200"))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/login"));
        }
}