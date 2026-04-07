package com.cmpt276.project.porject.workout.unit;

import com.cmpt276.project.porject.trackers.workouts.Workout;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class WorkoutTest {

    // test constructor
    @Test
    public void constructor_setsAllFieldsCorrectly() {
        LocalDateTime date = LocalDateTime.of(2025, 1, 15, 10, 0);
        Workout workout = new Workout("Running", 30, 300, date);

        assertEquals("Running", workout.getWorkoutName());
        assertEquals(30, workout.getDuration());
        assertEquals(300, workout.getCalsBurned());
        assertEquals(date, workout.getWorkoutDate());
    }

    // test that constructor sets createdAt on construction
    @Test
    public void constructor_setsCreatedAtOnConstruction() {
        LocalDateTime before = LocalDateTime.now().minusSeconds(1);
        Workout workout = new Workout("Cycling", 45, 400, LocalDateTime.now());
        LocalDateTime after = LocalDateTime.now().plusSeconds(1);

        assertNotNull(workout.getCreatedAt());
        assertTrue(workout.getCreatedAt().isAfter(before));
        assertTrue(workout.getCreatedAt().isBefore(after));
    }

    // test default constructor
    @Test
    public void defaultConstructor_doesNotThrow() {
        assertDoesNotThrow(() -> new Workout());
    }

    // setUserId

    @Test
    public void setUserId_updatesField() {
        Workout workout = new Workout("Swimming", 60, 500, LocalDateTime.now());
        workout.setUserId(42);

        assertEquals(42, workout.getUserId());
    }

    // test setWorkoutDate correctly updates workout date
    @Test
    public void setWorkoutDate_updatesField() {
        Workout workout = new Workout("Running", 30, 300, LocalDateTime.now());
        LocalDateTime newDate = LocalDateTime.of(2025, 6, 1, 8, 0);
        workout.setWorkoutDate(newDate);

        assertEquals(newDate, workout.getWorkoutDate());
    }

    // test setCalsBurned correctly updates cals burned
    @Test
    public void setCalsBurned_updatesField() {
        Workout workout = new Workout("Yoga", 60, 150, LocalDateTime.now());
        workout.setCalsBurned(200);

        assertEquals(200, workout.getCalsBurned());
    }

    // test setWorkoutName correctly updates workout name

    @Test
    public void setWorkoutName_updatesField() {
        Workout workout = new Workout("Running", 30, 300, LocalDateTime.now());
        workout.setWorkoutName("Sprinting");

        assertEquals("Sprinting", workout.getWorkoutName());
    }

    // test setDuration correctly updates duration
    @Test
    public void setDuration_updatesField() {
        Workout workout = new Workout("Running", 30, 300, LocalDateTime.now());
        workout.setDuration(60);

        assertEquals(60, workout.getDuration());
    }
}
