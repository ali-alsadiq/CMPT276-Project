package com.cmpt276.project.porject.trackers;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import com.cmpt276.project.porject.auth.User;
import com.cmpt276.project.porject.trackers.workouts.WorkoutApiService;
import com.cmpt276.project.porject.trackers.workouts.WorkoutRepository;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TrackerController.class)
public class TrackerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private WorkoutApiService workoutApiService;

    @MockitoBean
    private WorkoutRepository workoutRepository;

    @MockitoBean(name = "rankService")
    private com.cmpt276.project.porject.rank.RankService rankService;

    @Test
    public void testGetAddWorkoutPageWithUser() throws Exception {
        Mockito.when(rankService.getTierName(anyInt())).thenReturn("Bronze");
        User mockUser = new User("Test", "User", "testuser", "password", "USER");

        MockHttpSession session = new MockHttpSession();

        session.setAttribute("session_user", mockUser);

        mockMvc.perform(get("/add-workout").session(session))
                .andExpect(status().isOk());
    }
}