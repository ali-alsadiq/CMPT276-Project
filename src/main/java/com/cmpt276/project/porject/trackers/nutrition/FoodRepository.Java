package com.cmpt276.project.porject.trackers.nutrition;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Database access for food entity
 */
@Repository
public interface FoodRepository extends JpaRepository<Food, Integer> {

    List<Food> findByUserId(int userId);
    
    List<Workout> findByWorkoutNameAndUserId(String foodName, int userId);
    
    List<Workout> findByWorkoutName(String foodName);
    List<Workout> findByUserIdOrderByWorkoutDateDesc(int userId);
}