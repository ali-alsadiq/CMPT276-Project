package com.cmpt276.project.porject.trackers.workouts;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Database access for wrokout entity
 */
@Repository
public interface WorkoutRepository extends JpaRepository<Workout, Integer> {

    List<Workout> findByUserId(int userId);
    
    List<Workout> findByWorkoutNameAndUserId(String workoutName, int userId);
    
    List<Workout> findByWorkoutName(String workoutName);
    List<Workout> findByUserIdOrderByWorkoutDateDesc(int userId);
}