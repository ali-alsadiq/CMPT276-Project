package com.cmpt276.project.porject.workout.integration;

import com.cmpt276.project.porject.trackers.workouts.Workout;
import com.cmpt276.project.porject.trackers.workouts.WorkoutRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class WorkoutRepositoryTest {
    @Autowired
    private WorkoutRepository workoutRepository;

    private Workout buildWorkout(String name, int userId, int calories, LocalDateTime date) {
        Workout workout = new Workout(name, 30, calories, date);
        workout.setUserId(userId);
        return workout;
    }

    // test that a workout can be saved and found by its id
    @Test
    public void save_persistsWorkout() {
        Workout saved = workoutRepository.save(
                buildWorkout("Running", 1, 300, LocalDateTime.now()));

        Optional<Workout> found = workoutRepository.findById(saved.getId());

        assertTrue(found.isPresent());
        assertEquals("Running", found.get().getWorkoutName());
        assertEquals(300, found.get().getCalsBurned());
    }

    // test that a workout can be deleted by its id
    @Test
    public void delete_removesWorkout() {
        Workout saved = workoutRepository.save(
                buildWorkout("Yoga", 1, 150, LocalDateTime.now()));
        int id = saved.getId();

        workoutRepository.deleteById(id);

        assertFalse(workoutRepository.findById(id).isPresent());
    }

    // test that findByUserId returns only that user's workouts
    @Test
    public void findByUserId_returnsOnlyThatUsersWorkouts() {
        workoutRepository.save(buildWorkout("Running", 1, 300, LocalDateTime.now()));
        workoutRepository.save(buildWorkout("Cycling", 2, 400, LocalDateTime.now()));

        List<Workout> result = workoutRepository.findByUserId(1);

        assertEquals(1, result.size());
        assertEquals("Running", result.get(0).getWorkoutName());
    }

    // test that findByUserIdOrderByWorkoutDateDesc orders workouts by newest first
    @Test
    public void findByUserIdOrderByWorkoutDateDesc_ordersCorrectly() {
        LocalDateTime older = LocalDateTime.now().minusDays(2);
        LocalDateTime newer = LocalDateTime.now();

        workoutRepository.save(buildWorkout("Old Workout", 1, 200, older));
        workoutRepository.save(buildWorkout("New Workout", 1, 350, newer));

        List<Workout> result = workoutRepository.findByUserIdOrderByWorkoutDateDesc(1);

        assertEquals(2, result.size());
        assertEquals("New Workout", result.get(0).getWorkoutName());
        assertEquals("Old Workout", result.get(1).getWorkoutName());
    }

    // test that findByUserIdOrderByWorkoutDateDesc isolates workouts by user
    @Test
    public void findByUserIdOrderByWorkoutDateDesc_isolatesUserWorkouts() {
        workoutRepository.save(buildWorkout("User1 Workout", 1, 300, LocalDateTime.now()));
        workoutRepository.save(buildWorkout("User2 Workout", 2, 400, LocalDateTime.now()));

        List<Workout> result = workoutRepository.findByUserIdOrderByWorkoutDateDesc(1);

        assertEquals(1, result.size());
        assertEquals("User1 Workout", result.get(0).getWorkoutName());
    }

    // test that findByWorkoutName returns all workouts with that name
    @Test
    public void findByWorkoutName_returnsAllMatchingWorkouts() {
        workoutRepository.save(buildWorkout("Running", 1, 300, LocalDateTime.now()));
        workoutRepository.save(buildWorkout("Running", 2, 350, LocalDateTime.now()));
        workoutRepository.save(buildWorkout("Cycling", 1, 400, LocalDateTime.now()));

        List<Workout> result = workoutRepository.findByWorkoutName("Running");

        assertEquals(2, result.size());
    }

    // test that findByWorkoutNameAndUserId filters by both name and user
    @Test
    public void findByWorkoutNameAndUserId_filtersCorrectly() {
        workoutRepository.save(buildWorkout("Running", 1, 300, LocalDateTime.now()));
        workoutRepository.save(buildWorkout("Running", 2, 350, LocalDateTime.now()));
        workoutRepository.save(buildWorkout("Cycling", 1, 400, LocalDateTime.now()));

        List<Workout> result = workoutRepository.findByWorkoutNameAndUserId("Running", 1);

        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getUserId());
        assertEquals("Running", result.get(0).getWorkoutName());
    }

    // test that findByWorkoutNameAndUserId returns empty when no match
    @Test
    public void findByWorkoutNameAndUserId_noMatch_returnsEmpty() {
        workoutRepository.save(buildWorkout("Running", 1, 300, LocalDateTime.now()));

        List<Workout> result = workoutRepository.findByWorkoutNameAndUserId("Swimming", 1);

        assertTrue(result.isEmpty());
    }
}
