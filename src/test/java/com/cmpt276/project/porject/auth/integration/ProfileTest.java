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

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.mockito.ArgumentCaptor;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.springframework.test.web.servlet.MvcResult;

@WebMvcTest(UserController.class)
public class ProfileTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean(name = "rankService")
    private RankService rankService;

    private java.util.Map<String, String> defaultParams;

    @org.junit.jupiter.api.BeforeEach
    public void setup() {
        defaultParams = new java.util.HashMap<>();
        
        defaultParams.put("firstname", "User_test1");
        defaultParams.put("lastname", "TestLastname");
        defaultParams.put("sex", "Male");
        defaultParams.put("dateOfBirth", "2000-01-01");
        defaultParams.put("height", "175");
        defaultParams.put("weight", "72");
        defaultParams.put("weeklyCaloriesBurnedTarget", "2500");
        defaultParams.put("weeklyCaloriesConsumedTarget", "2000");
        defaultParams.put("weeklyProtienTarget", "500");
        defaultParams.put("weeklyCarbsTarget", "2000");
        defaultParams.put("weeklyFatsTarget", "500");
        defaultParams.put("weeklyFibreTarget", "200");
    }

    /* Tests that profile page redirects to login if not logged in. */
    @Test
    public void testGetProfilePageRedirectIfNotLoggedIn() throws Exception {
        mockMvc.perform(get("/profile"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    /* Tests that profile page loads for logged-in user. */
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

    /* Tests successful profile update. */
    @Test
    public void testUpdateProfileSuccess() throws Exception {
        User mockUser = new User("Old", "Name", "testuser1", "StrongPass1!", "USER");

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("session_user", mockUser);

        Mockito.when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder requestBuilder = post("/profile")
                .session(session);
        for (java.util.Map.Entry<String, String> entry : defaultParams.entrySet()) {
            requestBuilder.param(entry.getKey(), entry.getValue());
        }

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(view().name("users/profile"))
                .andExpect(model().attributeExists("success"));

        Mockito.verify(userRepository, Mockito.atLeastOnce()).save(any(User.class));
    }

    /* Tests profile update fails with invalid data. */
    @Test
    public void testUpdateProfileFailsWithInvalidData() throws Exception {
        User mockUser = new User("Old", "Name", "testuser1", "StrongPass1!", "USER");

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("session_user", mockUser);

        java.util.Map<String, String> badParams = new java.util.HashMap<>(defaultParams);
        badParams.put("firstname", "");
        badParams.put("sex", "");
        badParams.put("dateOfBirth", "3000-01-01");
        badParams.put("height", "20");
        badParams.put("weight", "900");
        badParams.put("weeklyCaloriesBurnedTarget", "100");

        org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder requestBuilder = post("/profile")
                .session(session);
        for (java.util.Map.Entry<String, String> entry : badParams.entrySet()) {
            requestBuilder.param(entry.getKey(), entry.getValue());
        }

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(view().name("users/profile"))
                .andExpect(model().attributeExists("firstnameError"))
                .andExpect(model().attributeExists("sexError"))
                .andExpect(model().attributeExists("dateOfBirthError"))
                .andExpect(model().attributeExists("heightError"))
                .andExpect(model().attributeExists("weightError"))
                .andExpect(model().attributeExists("weeklyCaloriesBurnedTargetError"));

        Mockito.verify(userRepository, Mockito.never()).save(any(User.class));
    }

    /* Tests profile update redirects if not logged in. */
    @Test
    public void testUpdateProfileRedirectIfNotLoggedIn() throws Exception {
        org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder requestBuilder = post("/profile");
        for (java.util.Map.Entry<String, String> entry : defaultParams.entrySet()) {
            requestBuilder.param(entry.getKey(), entry.getValue());
        }

        mockMvc.perform(requestBuilder)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    // -- Profile Bounds & Date of Birth Tests --

    private void testField(String fieldToOverride, String overrideValue, String errorKey, boolean expectError)
            throws Exception {
        User mockUser = new User("Old", "Name", "testuser1", "StrongPass1!", "USER");
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("session_user", mockUser);

        java.util.Map<String, String> params = new java.util.HashMap<>(defaultParams);
        params.put(fieldToOverride, overrideValue);

        org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder requestBuilder = post("/profile")
                .session(session);
        for (java.util.Map.Entry<String, String> entry : params.entrySet()) {
            requestBuilder.param(entry.getKey(), entry.getValue());
        }

        if (expectError) {
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andExpect(model().attributeExists(errorKey));
        } else {
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andExpect(model().attributeDoesNotExist(errorKey));
        }
    }

    // Persistence & Session Tests

    @Test
    public void profile_validSubmission_doesNotChangeUserSetTargets() throws Exception {
        User mockUser = new User("Old", "Name", "testuser1", "StrongPass1!", "USER");
        mockUser.setUserSetTargets(true);

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("session_user", mockUser);

        Mockito.when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        var requestBuilder = post("/profile").session(session);
        defaultParams.forEach(requestBuilder::param);

        mockMvc.perform(requestBuilder).andExpect(status().isOk());

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        Mockito.verify(userRepository, Mockito.atLeastOnce()).save(userCaptor.capture());
        assertTrue(userCaptor.getValue().getUserSetTargets());
    }

    @Test
    public void profile_validSubmission_updatesSessionUser() throws Exception {
        User mockUser = new User("Old", "Name", "testuser1", "StrongPass1!", "USER");
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("session_user", mockUser);

        Mockito.when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        var requestBuilder = post("/profile").session(session);
        defaultParams.forEach(requestBuilder::param);

        MvcResult result = mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andReturn();

        User updatedSessionUser = (User) result.getRequest().getSession().getAttribute("session_user");
        assertEquals("User_test1", updatedSessionUser.getFirstname());
        assertEquals("TestLastname", updatedSessionUser.getLastname());
    }

    /* Height Bounds */
    @Test
    public void heightBounds_exactly50_isAccepted() throws Exception {
        testField("height", "50", "heightError", false);
    }

    @Test
    public void heightBounds_49point9_isRejected() throws Exception {
        testField("height", "49.9", "heightError", true);
    }

    @Test
    public void heightBounds_exactly300_isAccepted() throws Exception {
        testField("height", "300", "heightError", false);
    }

    @Test
    public void heightBounds_300point1_isRejected() throws Exception {
        testField("height", "300.1", "heightError", true);
    }

    /* Weight Bounds */
    @Test
    public void weightBounds_exactly20_isAccepted() throws Exception {
        testField("weight", "20", "weightError", false);
    }

    @Test
    public void weightBounds_19point9_isRejected() throws Exception {
        testField("weight", "19.9", "weightError", true);
    }

    @Test
    public void weightBounds_exactly500_isAccepted() throws Exception {
        testField("weight", "500", "weightError", false);
    }

    @Test
    public void weightBounds_500point1_isRejected() throws Exception {
        testField("weight", "500.1", "weightError", true);
    }

    /* Calorie Burned Bounds */
    @Test
    public void calBurned_exactly500_isAccepted() throws Exception {
        testField("weeklyCaloriesBurnedTarget", "500", "weeklyCaloriesBurnedTargetError", false);
    }

    @Test
    public void calBurned_499point9_isRejected() throws Exception {
        testField("weeklyCaloriesBurnedTarget", "499.9", "weeklyCaloriesBurnedTargetError", true);
    }

    @Test
    public void calBurned_exactly10000_isAccepted() throws Exception {
        testField("weeklyCaloriesBurnedTarget", "10000", "weeklyCaloriesBurnedTargetError", false);
    }

    @Test
    public void calBurned_10000point1_isRejected() throws Exception {
        testField("weeklyCaloriesBurnedTarget", "10000.1", "weeklyCaloriesBurnedTargetError", true);
    }

    /* Calorie Consumed Bounds */
    @Test
    public void calConsumed_exactly500_isAccepted() throws Exception {
        testField("weeklyCaloriesConsumedTarget", "500", "weeklyCaloriesConsumedTargetError", false);
    }

    @Test
    public void calConsumed_499point9_isRejected() throws Exception {
        testField("weeklyCaloriesConsumedTarget", "499.9", "weeklyCaloriesConsumedTargetError", true);
    }

    @Test
    public void calConsumed_exactly10000_isAccepted() throws Exception {
        testField("weeklyCaloriesConsumedTarget", "10000", "weeklyCaloriesConsumedTargetError", false);
    }

    @Test
    public void calConsumed_10000point1_isRejected() throws Exception {
        testField("weeklyCaloriesConsumedTarget", "10000.1", "weeklyCaloriesConsumedTargetError", true);
    }

    /* Macro Bounds (Protein) */
    @Test
    public void protein_exactly200_isAccepted() throws Exception {
        testField("weeklyProtienTarget", "200", "weeklyProtienTargetError", false);
    }

    @Test
    public void protein_199point9_isRejected() throws Exception {
        testField("weeklyProtienTarget", "199.9", "weeklyProtienTargetError", true);
    }

    @Test
    public void protein_exactly10000_isAccepted() throws Exception {
        testField("weeklyProtienTarget", "10000", "weeklyProtienTargetError", false);
    }

    @Test
    public void protein_10000point1_isRejected() throws Exception {
        testField("weeklyProtienTarget", "10000.1", "weeklyProtienTargetError", true);
    }

    /* Macro Bounds (Carbs) */
    @Test
    public void carbs_exactly200_isAccepted() throws Exception {
        testField("weeklyCarbsTarget", "200", "weeklyCarbsTargetError", false);
    }

    @Test
    public void carbs_199point9_isRejected() throws Exception {
        testField("weeklyCarbsTarget", "199.9", "weeklyCarbsTargetError", true);
    }

    @Test
    public void carbs_exactly10000_isAccepted() throws Exception {
        testField("weeklyCarbsTarget", "10000", "weeklyCarbsTargetError", false);
    }

    @Test
    public void carbs_10000point1_isRejected() throws Exception {
        testField("weeklyCarbsTarget", "10000.1", "weeklyCarbsTargetError", true);
    }

    /* Macro Bounds (Fats) */
    @Test
    public void fats_exactly200_isAccepted() throws Exception {
        testField("weeklyFatsTarget", "200", "weeklyFatsTargetError", false);
    }

    @Test
    public void fats_199point9_isRejected() throws Exception {
        testField("weeklyFatsTarget", "199.9", "weeklyFatsTargetError", true);
    }

    @Test
    public void fats_exactly10000_isAccepted() throws Exception {
        testField("weeklyFatsTarget", "10000", "weeklyFatsTargetError", false);
    }

    @Test
    public void fats_10000point1_isRejected() throws Exception {
        testField("weeklyFatsTarget", "10000.1", "weeklyFatsTargetError", true);
    }

    /* Macro Bounds (Fibre) */
    @Test
    public void fibre_exactly200_isAccepted() throws Exception {
        testField("weeklyFibreTarget", "200", "weeklyFibreTargetError", false);
    }

    @Test
    public void fibre_199point9_isRejected() throws Exception {
        testField("weeklyFibreTarget", "199.9", "weeklyFibreTargetError", true);
    }

    @Test
    public void fibre_exactly10000_isAccepted() throws Exception {
        testField("weeklyFibreTarget", "10000", "weeklyFibreTargetError", false);
    }

    @Test
    public void fibre_10000point1_isRejected() throws Exception {
        testField("weeklyFibreTarget", "10000.1", "weeklyFibreTargetError", true);
    }

    /* Date of Birth */
    @Test
    public void dob_yesterday_isValid() throws Exception {
        testField("dateOfBirth", java.time.LocalDate.now().minusDays(1).toString(), "dateOfBirthError", false);
    }

    @Test
    public void dob_today_isRejected() throws Exception {
        testField("dateOfBirth", java.time.LocalDate.now().toString(), "dateOfBirthError", true);
    }

    @Test
    public void dob_futureDate_isRejected() throws Exception {
        testField("dateOfBirth", java.time.LocalDate.now().plusDays(1).toString(), "dateOfBirthError", true);
    }

    @Test
    public void dob_nonParseableString_showsParseError() throws Exception {
        testField("dateOfBirth", "not-a-date", "dateOfBirthError", true);
    }

    @Test
    public void dob_leapYearDate_validPastDate_isAccepted() throws Exception {
        testField("dateOfBirth", "2000-02-29", "dateOfBirthError", false);
    }

}
