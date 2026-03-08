package com.cmpt276.project.porject.meals;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for accessing meal entry data.
 */
public interface MealEntryRepository extends JpaRepository<MealEntry, Integer> {

    /**
     * Finds all meals for a user ordered from newest to oldest.
     * 
     * @param uid User ID
     * @return List of meals for that user.
     */
    List<MealEntry> findByUserUidOrderByEatenAtDesc(int uid);

    /**
     * Finds all meals for a user within a time range.
     * 
     * @param uid   User ID
     * @param start Start date/time
     * @param end   End date/time
     * @return List of meals in that range.
     */
    List<MealEntry> findByUserUidAndEatenAtBetween(int uid, LocalDateTime start, LocalDateTime end);
}
