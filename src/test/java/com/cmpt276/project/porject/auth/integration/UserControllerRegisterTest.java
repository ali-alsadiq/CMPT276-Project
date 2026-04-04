package com.cmpt276.project.porject.auth.integration;

import com.cmpt276.project.porject.auth.User;
import com.cmpt276.project.porject.auth.UserController;
import com.cmpt276.project.porject.auth.UserRepository;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import com.cmpt276.project.porject.rank.RankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerRegisterTest {

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private UserRepository userRepository;

        @MockitoBean(name = "rankService")
        private RankService rankService;

        /* Tests that user registers successfully. */
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

        /* Tests that user registers with a weak password. */
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

        /* Tests empty POST errors on registration. */
        @Test
        public void testRegisterEmptyPostError() throws Exception {
                mockMvc.perform(post("/register")
                                .param("firstname", "")
                                .param("lastname", "")
                                .param("username", "")
                                .param("password", ""))
                                .andExpect(status().isOk())
                                .andExpect(view().name("users/register"))
                                .andExpect(model().attributeExists("firstnameError"))
                                .andExpect(model().attributeExists("lastnameError"))
                                .andExpect(model().attributeExists("usernameError"))
                                .andExpect(model().attributeExists("passwordError"));

                Mockito.verify(userRepository, Mockito.never()).save(any(User.class));
        }

        /* Tests username containing space error. */
        @Test
        public void testRegisterUsernameSpaceError() throws Exception {
                mockMvc.perform(post("/register")
                                .param("firstname", "Test")
                                .param("lastname", "User")
                                .param("username", "invalid username")
                                .param("password", "StrongPass1!"))
                                .andExpect(status().isOk())
                                .andExpect(view().name("users/register"))
                                .andExpect(model().attributeExists("usernameError"))
                                .andExpect(model().attribute("usernameError", true));

                Mockito.verify(userRepository, Mockito.never()).save(any(User.class));
        }

        /* Tests username already exists error. */
        @Test
        public void testRegisterDuplicateUsernameError() throws Exception {
                User existingUser = new User("Test", "User", "existingUser", "StrongPass1!", "USER");
                Mockito.when(userRepository.findByUsername("existingUser"))
                                .thenReturn(Collections.singletonList(existingUser));

                mockMvc.perform(post("/register")
                                .param("firstname", "Test")
                                .param("lastname", "User")
                                .param("username", "existingUser")
                                .param("password", "StrongPass1!"))
                                .andExpect(status().isOk())
                                .andExpect(view().name("users/register"))
                                .andExpect(model().attributeExists("usernameError"))
                                .andExpect(model().attribute("usernameError", true));

                Mockito.verify(userRepository, Mockito.never()).save(any(User.class));
        }

        /* Tests that a user cannot access the admin dashboard. */
        @Test
        public void testAdminDashboardAccessDenied() throws Exception {
                User standardUser = new User("Test", "User", "normie", "pass123", "USER");

                MockHttpSession session = new MockHttpSession();
                session.setAttribute("session_user", standardUser);

                mockMvc.perform(get("/admin-dashboard").session(session))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/login"));
        }
}