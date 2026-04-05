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

    // Login

    /* Tests login page is displayed when the user visits the login page. */
    @Test
    public void testGetLoginPage() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("users/login"));
    }

    /* Admin Log In (new user) redirects to admin-dashboard. */
    @Test
    public void login_newUserAdmin_redirectsToAdminDashboard() throws Exception {
        User mockUser = new User("Test", "Admin", "newadmin", "StrongPass1!", "ADMIN");
        mockUser.setUserSetTargets(false);
        List<User> mockList = Collections.singletonList(mockUser);

        Mockito.when(userRepository.findByUsernameAndPassword("newadmin", "StrongPass1!"))
                .thenReturn(mockList);

        mockMvc.perform(post("/login")
                .param("username", "newadmin")
                .param("password", "StrongPass1!"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin-dashboard"))
                .andExpect(request().sessionAttribute("session_user", mockUser));
    }

    /* Admin Log In (standard) redirects to admin-dashboard. */
    @Test
    public void login_standardAdmin_redirectsToAdminDashboard() throws Exception {
        User mockUser = new User("Test", "Admin", "admin1", "StrongPass1!", "ADMIN");
        mockUser.setUserSetTargets(true);
        List<User> mockList = Collections.singletonList(mockUser);

        Mockito.when(userRepository.findByUsernameAndPassword("admin1", "StrongPass1!"))
                .thenReturn(mockList);

        mockMvc.perform(post("/login")
                .param("username", "admin1")
                .param("password", "StrongPass1!"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin-dashboard"))
                .andExpect(request().sessionAttribute("session_user", mockUser));
    }

    /* User Log In (new user) goes to onboarding. */
    @Test
    public void login_newUser_redirectsToOnboarding() throws Exception {
        User mockUser = new User("Test", "User", "newuser", "StrongPass1!", "USER");
        mockUser.setUserSetTargets(false);
        List<User> mockList = Collections.singletonList(mockUser);

        Mockito.when(userRepository.findByUsernameAndPassword("newuser", "StrongPass1!"))
                .thenReturn(mockList);

        mockMvc.perform(post("/login")
                .param("username", "newuser")
                .param("password", "StrongPass1!"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/onBoarding"))
                .andExpect(request().sessionAttribute("session_user", mockUser));
    }

    /* User log in (standard) goes to /dashboard. */
    @Test
    public void login_standardUser_redirectsToDashboard() throws Exception {
        User mockUser = new User("Test", "User", "stduser", "StrongPass1!", "USER");
        mockUser.setUserSetTargets(true);
        List<User> mockList = Collections.singletonList(mockUser);

        Mockito.when(userRepository.findByUsernameAndPassword("stduser", "StrongPass1!"))
                .thenReturn(mockList);

        mockMvc.perform(post("/login")
                .param("username", "stduser")
                .param("password", "StrongPass1!"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dashboard"))
                .andExpect(request().sessionAttribute("session_user", mockUser));
    }

    /* User login with blank password. */
    @Test
    public void login_missingPassword_showsFieldError() throws Exception {
        mockMvc.perform(post("/login")
                .param("username", "testuser")
                .param("password", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("users/login"))
                .andExpect(model().attributeExists("passwordError"));
    }

    /* User login with blank username. */
    @Test
    public void login_missingUsername_showsFieldError() throws Exception {
        mockMvc.perform(post("/login")
                .param("username", "")
                .param("password", "StrongPass1!"))
                .andExpect(status().isOk())
                .andExpect(view().name("users/login"))
                .andExpect(model().attributeExists("usernameError"));
    }

    /* User login with wrong credentials. */
    @Test
    public void login_wrongCredentials_showsError() throws Exception {
        Mockito.when(userRepository.findByUsernameAndPassword("testuser2", "wrongpw"))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(post("/login")
                .param("username", "testuser2")
                .param("password", "wrongpw"))
                .andExpect(status().isOk())
                .andExpect(view().name("users/login"))
                .andExpect(model().attributeExists("error"))
                .andExpect(request().sessionAttributeDoesNotExist("session_user"));
    }

    /* User with no rank profile triggers userRepository.save(). */
    @Test
    public void login_noRankProfile_triggersUserRepositorySave() throws Exception {
        User mockUser = new User("Test", "User", "stduser", "StrongPass1!", "USER");
        mockUser.setUserSetTargets(true);
        mockUser.getRankProfile().setId(0);
        List<User> mockList = Collections.singletonList(mockUser);

        Mockito.when(userRepository.findByUsernameAndPassword("stduser", "StrongPass1!"))
                .thenReturn(mockList);

        mockMvc.perform(post("/login")
                .param("username", "stduser")
                .param("password", "StrongPass1!"))
                .andExpect(status().is3xxRedirection());

        Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any(User.class));
    }

    // Register

    /* Tests that the register page is displayed. */
    @Test
    public void testGetRegisterPage() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("users/register"));
    }

    /* Tests that user registers successfully. */
    @Test
    public void testRegisterSuccess() throws Exception {
        mockMvc.perform(post("/register")
                .param("firstname", "Test")
                .param("lastname", "User_test1")
                .param("username", "user_test1")
                .param("password", "StrongPass1!")
                .param("role", "USER"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

        Mockito.verify(userRepository, Mockito.times(1)).save(any(User.class));
    }

    /* Tests that user registers with a weak password. */
    @Test
    public void testRegisterWeakPassword() throws Exception {
        mockMvc.perform(post("/register")
                .param("firstname", "Test")
                .param("lastname", "User_test1")
                .param("username", "user_test1")
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
                .param("lastname", "User_test1")
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
        User existingUser = new User("Test", "User_test1", "user_test1", "StrongPass1!", "USER");
        Mockito.when(userRepository.findByUsername("user_test1"))
                .thenReturn(Collections.singletonList(existingUser));

        mockMvc.perform(post("/register")
                .param("firstname", "Test")
                .param("lastname", "User_test1")
                .param("username", "user_test1")
                .param("password", "StrongPass1!"))
                .andExpect(status().isOk())
                .andExpect(view().name("users/register"))
                .andExpect(model().attributeExists("usernameError"))
                .andExpect(model().attribute("usernameError", true));

        Mockito.verify(userRepository, Mockito.never()).save(any(User.class));
    }

    /* Tests that a standard user cannot access the admin dashboard. */
    @Test
    public void testAdminDashboardAccessDenied() throws Exception {
        User standardUser = new User("Test", "User_test1", "user_test1", "pass123", "USER");

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("session_user", standardUser);

        mockMvc.perform(get("/admin-dashboard").session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }
}
