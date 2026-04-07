package com.cmpt276.project.porject.auth.unit;

import com.cmpt276.project.porject.auth.User;
import com.cmpt276.project.porject.auth.UserController;
import com.cmpt276.project.porject.auth.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.test.util.ReflectionTestUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ProfileTest {
    private UserController userController;
    private UserRepository userRepository;
    private HttpServletRequest request;
    private HttpSession session;
    private User mockUser;

    @BeforeEach
    public void setUp() {
        userRepository = mock(UserRepository.class);
        userController = new UserController();

        // Inject dependencies manually for pure unit testing
        ReflectionTestUtils.setField(userController, "userRepository", userRepository);

        request = mock(HttpServletRequest.class);
        session = mock(HttpSession.class);
        mockUser = new User("User_test1", "TestLastname", "testuser", "Password1!", "USER");

        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("session_user")).thenReturn(mockUser);
    }

    private void testField(String field, String value, String errorAttr, boolean expectError) {
        Map<String, String> profileData = new HashMap<>();
        profileData.put("firstname", "User_test1");
        profileData.put("lastname", "TestLastname");
        profileData.put("sex", "Male");
        profileData.put("dateOfBirth", "2000-01-01");
        profileData.put("height", "175");
        profileData.put("weight", "72");
        profileData.put("weeklyCaloriesBurnedTarget", "2500");
        profileData.put("weeklyCaloriesConsumedTarget", "2000");
        profileData.put("weeklyProtienTarget", "500");
        profileData.put("weeklyCarbsTarget", "2000");
        profileData.put("weeklyFatsTarget", "500");
        profileData.put("weeklyFibreTarget", "200");

        profileData.put(field, value);

        Model model = new ExtendedModelMap();
        userController.updateProfile(profileData, model, request);

        if (expectError) {
            assertTrue(model.containsAttribute(errorAttr),
                    "Expected error attribute " + errorAttr + " for field " + field + " with value " + value);
        } else {
            assertFalse(model.containsAttribute(errorAttr),
                    "Did not expect error attribute " + errorAttr + " for field " + field + " with value " + value);
        }
    }

    // Height Bounds
    @Test
    public void heightBounds_50_isAccepted() {
        testField("height", "50.0", "heightError", false);
    }

    @Test
    public void heightBounds_49point9_isRejected() {
        testField("height", "49.9", "heightError", true);
    }

    @Test
    public void heightBounds_300_isAccepted() {
        testField("height", "300.0", "heightError", false);
    }

    @Test
    public void heightBounds_300point1_isRejected() {
        testField("height", "300.1", "heightError", true);
    }

    // Weight Bounds
    @Test
    public void weightBounds_20_isAccepted() {
        testField("weight", "20.0", "weightError", false);
    }

    @Test
    public void weightBounds_19point9_isRejected() {
        testField("weight", "19.9", "weightError", true);
    }

    @Test
    public void weightBounds_500_isAccepted() {
        testField("weight", "500.0", "weightError", false);
    }

    @Test
    public void weightBounds_500point1_isRejected() {
        testField("weight", "500.1", "weightError", true);
    }

    // Calories Burned Targets
    @Test
    public void calBurnedTarget_500_isAccepted() {
        testField("weeklyCaloriesBurnedTarget", "500", "weeklyCaloriesBurnedTargetError", false);
    }



    @Test
    public void calBurnedTarget_10000_isAccepted() {
        testField("weeklyCaloriesBurnedTarget", "10000", "weeklyCaloriesBurnedTargetError", false);
    }

    @Test
    public void calBurnedTarget_10000point1_isRejected() {
        testField("weeklyCaloriesBurnedTarget", "10000.1", "weeklyCaloriesBurnedTargetError", true);
    }

    // Macro Targets
    @Test
    public void macroTarget_200_isAccepted() {
        testField("weeklyProtienTarget", "200", "weeklyProtienTargetError", false);
        testField("weeklyCarbsTarget", "200", "weeklyCarbsTargetError", false);
        testField("weeklyFatsTarget", "200", "weeklyFatsTargetError", false);
        testField("weeklyFibreTarget", "200", "weeklyFibreTargetError", false);
    }



    @Test
    public void macroTarget_10000_isAccepted() {
        testField("weeklyProtienTarget", "10000", "weeklyProtienTargetError", false);
        testField("weeklyCarbsTarget", "10000", "weeklyCarbsTargetError", false);
        testField("weeklyFatsTarget", "10000", "weeklyFatsTargetError", false);
        testField("weeklyFibreTarget", "10000", "weeklyFibreTargetError", false);
    }

    }
