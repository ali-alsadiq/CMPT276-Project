package com.cmpt276.project.porject.rank.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import com.cmpt276.project.porject.rank.RankProfile;
import com.cmpt276.project.porject.rank.RankProfileRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class RankProfileRepositoryTest {

    @Autowired
    private RankProfileRepository rankProfileRepository;

    // Basic CRUD
    @Test
    public void save_persistsRankProfile() {
        RankProfile profile = new RankProfile();
        profile.setRr(500);

        RankProfile saved = rankProfileRepository.save(profile);

        assertNotNull(saved.getId());
        assertTrue(saved.getId() > 0);
    }

    @Test
    public void findById_returnsPersistedProfile() {
        RankProfile profile = new RankProfile();
        profile.setRr(300);

        RankProfile saved = rankProfileRepository.save(profile);
        Optional<RankProfile> found = rankProfileRepository.findById(saved.getId());

        assertTrue(found.isPresent());
        assertEquals(300, found.get().getRr());
    }

    @Test
    public void findById_nonExistentId_returnsEmpty() {
        Optional<RankProfile> found = rankProfileRepository.findById(Integer.MAX_VALUE);
        assertFalse(found.isPresent());
    }

    @Test
    public void delete_removesProfile() {
        RankProfile profile = new RankProfile();
        profile.setRr(100);

        RankProfile saved = rankProfileRepository.save(profile);
        int savedId = saved.getId();

        rankProfileRepository.deleteById(savedId);

        assertFalse(rankProfileRepository.findById(savedId).isPresent());
    }

    // Persistence
    @Test
    public void save_persistsRrFieldCorrectly() {
        RankProfile profile = new RankProfile();
        profile.setRr(1750);

        RankProfile saved = rankProfileRepository.save(profile);
        RankProfile reloaded = rankProfileRepository.findById(saved.getId()).orElseThrow();

        assertEquals(1750, reloaded.getRr());
    }

    @Test
    public void save_persistsFoodLoggedDaysMask() {
        RankProfile profile = new RankProfile();
        profile.setFoodLoggedDaysMask(0b1010101); // Mon, Wed, Fri, Sun

        RankProfile saved = rankProfileRepository.save(profile);
        RankProfile reloaded = rankProfileRepository.findById(saved.getId()).orElseThrow();

        assertEquals(0b1010101, reloaded.getFoodLoggedDaysMask());
    }

    @Test
    public void save_persistsWorkoutLoggedDaysMask() {
        RankProfile profile = new RankProfile();
        profile.setWorkoutLoggedDaysMask(0b1111111); // all 7 days

        RankProfile saved = rankProfileRepository.save(profile);
        RankProfile reloaded = rankProfileRepository.findById(saved.getId()).orElseThrow();

        assertEquals(0b1111111, reloaded.getWorkoutLoggedDaysMask());
    }

    @Test
    public void save_persistsFoodRewardWeekStart() {
        LocalDate weekStart = LocalDate.of(2026, 4, 6);
        RankProfile profile = new RankProfile();
        profile.setFoodRewardWeekStart(weekStart);

        RankProfile saved = rankProfileRepository.save(profile);
        RankProfile reloaded = rankProfileRepository.findById(saved.getId()).orElseThrow();

        assertEquals(weekStart, reloaded.getFoodRewardWeekStart());
    }

    @Test
    public void save_persistsWorkoutRewardWeekStart() {
        LocalDate weekStart = LocalDate.of(2026, 3, 30);
        RankProfile profile = new RankProfile();
        profile.setWorkoutRewardWeekStart(weekStart);

        RankProfile saved = rankProfileRepository.save(profile);
        RankProfile reloaded = rankProfileRepository.findById(saved.getId()).orElseThrow();

        assertEquals(weekStart, reloaded.getWorkoutRewardWeekStart());
    }

    @Test
    public void save_persistsWeeklyFoodGoalAwarded() {
        RankProfile profile = new RankProfile();
        profile.setWeeklyFoodGoalAwarded(true);

        RankProfile saved = rankProfileRepository.save(profile);
        RankProfile reloaded = rankProfileRepository.findById(saved.getId()).orElseThrow();

        assertTrue(reloaded.isWeeklyFoodGoalAwarded());
    }

    @Test
    public void save_persistsWeeklyWorkoutGoalAwarded() {
        RankProfile profile = new RankProfile();
        profile.setWeeklyWorkoutGoalAwarded(true);

        RankProfile saved = rankProfileRepository.save(profile);
        RankProfile reloaded = rankProfileRepository.findById(saved.getId()).orElseThrow();

        assertTrue(reloaded.isWeeklyWorkoutGoalAwarded());
    }

    @Test
    public void save_persistsFoodStreakBonusAwarded() {
        RankProfile profile = new RankProfile();
        profile.setWeeklyFoodStreakBonusAwarded(true);

        RankProfile saved = rankProfileRepository.save(profile);
        RankProfile reloaded = rankProfileRepository.findById(saved.getId()).orElseThrow();

        assertTrue(reloaded.isWeeklyFoodStreakBonusAwarded());
    }

    @Test
    public void save_persistsWorkoutStreakBonusAwarded() {
        RankProfile profile = new RankProfile();
        profile.setWeeklyWorkoutStreakBonusAwarded(true);

        RankProfile saved = rankProfileRepository.save(profile);
        RankProfile reloaded = rankProfileRepository.findById(saved.getId()).orElseThrow();

        assertTrue(reloaded.isWeeklyWorkoutStreakBonusAwarded());
    }

    @Test
    public void save_persistsLastFoodPenaltyWeekStart() {
        LocalDate penaltyWeek = LocalDate.of(2026, 3, 23);
        RankProfile profile = new RankProfile();
        profile.setLastFoodPenaltyWeekStart(penaltyWeek);

        RankProfile saved = rankProfileRepository.save(profile);
        RankProfile reloaded = rankProfileRepository.findById(saved.getId()).orElseThrow();

        assertEquals(penaltyWeek, reloaded.getLastFoodPenaltyWeekStart());
    }

    @Test
    public void save_persistsLastWorkoutPenaltyWeekStart() {
        LocalDate penaltyWeek = LocalDate.of(2026, 3, 16);
        RankProfile profile = new RankProfile();
        profile.setLastWorkoutPenaltyWeekStart(penaltyWeek);

        RankProfile saved = rankProfileRepository.save(profile);
        RankProfile reloaded = rankProfileRepository.findById(saved.getId()).orElseThrow();

        assertEquals(penaltyWeek, reloaded.getLastWorkoutPenaltyWeekStart());
    }

    // Updates
    @Test
    public void update_rr_persistsNewValue() {
        RankProfile profile = new RankProfile();
        profile.setRr(100);
        RankProfile saved = rankProfileRepository.save(profile);

        saved.setRr(950);
        rankProfileRepository.save(saved);

        RankProfile reloaded = rankProfileRepository.findById(saved.getId()).orElseThrow();
        assertEquals(950, reloaded.getRr());
    }

    @Test
    public void update_foodMask_persistsNewValue() {
        RankProfile profile = new RankProfile();
        profile.setFoodLoggedDaysMask(0b0000001);
        RankProfile saved = rankProfileRepository.save(profile);

        saved.setFoodLoggedDaysMask(0b1111111);
        rankProfileRepository.save(saved);

        RankProfile reloaded = rankProfileRepository.findById(saved.getId()).orElseThrow();
        assertEquals(0b1111111, reloaded.getFoodLoggedDaysMask());
    }

    // =========================================================
    // DEFAULTS ON SAVE
    // =========================================================

    @Test
    public void newProfile_defaultRrIsZero() {
        RankProfile profile = rankProfileRepository.save(new RankProfile());
        RankProfile reloaded = rankProfileRepository.findById(profile.getId()).orElseThrow();

        assertEquals(0, reloaded.getRr());
    }

    @Test
    public void newProfile_defaultMasksAreZero() {
        RankProfile profile = rankProfileRepository.save(new RankProfile());
        RankProfile reloaded = rankProfileRepository.findById(profile.getId()).orElseThrow();

        assertEquals(0, reloaded.getFoodLoggedDaysMask());
        assertEquals(0, reloaded.getWorkoutLoggedDaysMask());
    }

    @Test
    public void newProfile_defaultBooleanFlagsAreFalse() {
        RankProfile profile = rankProfileRepository.save(new RankProfile());
        RankProfile reloaded = rankProfileRepository.findById(profile.getId()).orElseThrow();

        assertFalse(reloaded.isWeeklyFoodGoalAwarded());
        assertFalse(reloaded.isWeeklyWorkoutGoalAwarded());
        assertFalse(reloaded.isWeeklyFoodStreakBonusAwarded());
        assertFalse(reloaded.isWeeklyWorkoutStreakBonusAwarded());
    }

    @Test
    public void newProfile_defaultDateFieldsAreNull() {
        RankProfile profile = rankProfileRepository.save(new RankProfile());
        RankProfile reloaded = rankProfileRepository.findById(profile.getId()).orElseThrow();

        assertNull(reloaded.getFoodRewardWeekStart());
        assertNull(reloaded.getWorkoutRewardWeekStart());
        assertNull(reloaded.getLastFoodPenaltyWeekStart());
        assertNull(reloaded.getLastWorkoutPenaltyWeekStart());
    }

    // Find All
    @Test
    public void findAll_returnsAllSavedProfiles() {
        rankProfileRepository.save(new RankProfile());
        rankProfileRepository.save(new RankProfile());
        rankProfileRepository.save(new RankProfile());

        List<RankProfile> all = rankProfileRepository.findAll();

        assertTrue(all.size() >= 3);
    }
}
