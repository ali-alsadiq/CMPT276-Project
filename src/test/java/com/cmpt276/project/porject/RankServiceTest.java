package com.cmpt276.project.porject;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RankServiceTest {
    private RankService rankService = new RankService();

    @Test
    public void testCalculateTier() {
        // Test standard rank values
        assertEquals("Bronze I", rankService.calculateRank(0));
        assertEquals("Bronze I", rankService.calculateRank(99));

        // Test jumping into a new rank
        assertEquals("Bronze II", rankService.calculateRank(100));
        assertEquals("Silver II", rankService.calculateRank(450));

        // Test edge of last and next rank
        assertEquals("Gold III", rankService.calculateRank(899));
        assertEquals("Platinum I", rankService.calculateRank(900));

        // Test the maximum cap
        assertEquals("5000 RR", rankService.calculateRank(5000));
    }

    @Test
    public void testCalculatePointsToNextTier() {
        // Test standard calculation values
        assertEquals(66, rankService.calculatePointsToNextTier(234));
        assertEquals(1, rankService.calculatePointsToNextTier(199));

        // Test the maximum cap
        assertEquals(0, rankService.calculatePointsToNextTier(1500));
        assertEquals(0, rankService.calculatePointsToNextTier(2250));
    }
}