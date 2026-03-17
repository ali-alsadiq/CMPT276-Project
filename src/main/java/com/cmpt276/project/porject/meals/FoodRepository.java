package com.cmpt276.project.porject.meals;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


/**
 * Database access for food entity
 */
@Repository
public interface FoodRepository extends JpaRepository<Food, Integer> {
}
