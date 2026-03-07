package com.cmpt276.project.porject.auth;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
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
        public void testLoginSuccessForUser() throws Exception {
                User mockUser = new User("Test", "User", "testuser1", "StrongPass1!", "USER");
                List<User> mockList = Collections.singletonList(mockUser);

                Mockito.when(userRepository.findByUsernameAndPassword("testuser1", "StrongPass1!"))
                                .thenReturn(mockList);

                mockMvc.perform(post("/login")
                                .param("firstname", "Test")
                                .param("lastname", "User")
                                .param("username", "testuser1")
                                .param("password", "StrongPass1!"))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/dashboard"))
                                .andExpect(request().sessionAttribute("session_user", mockUser));
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
                                .andExpect(redirectedUrl("/dashboard"))
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

                mockMvc.perform(get("/users/view").session(session))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/login"));
        }
}