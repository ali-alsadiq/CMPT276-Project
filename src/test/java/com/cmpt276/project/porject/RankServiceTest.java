package com.cmpt276.project.porject;

import org.junit.jupiter.api.Test;
import com.cmpt276.project.porject.rank.RankService;

import static org.junit.jupiter.api.Assertions.*;

public class RankServiceTest {
    
    private RankService rankService = new RankService();
    
    @Test
    public void testCalculateRank() {
        // Test standard rank values
        assertEquals("Bronze I", rankService.getTierName(0));
        assertEquals("Bronze I", rankService.getTierName(99));

        // Test jumping into a new rank
        assertEquals("Bronze II", rankService.getTierName(100));
        assertEquals("Silver II", rankService.getTierName(450));

        // Test edge of last and next rank
        assertEquals("Gold III", rankService.getTierName(899));
        assertEquals("Platinum I", rankService.getTierName(900));

        // Test the maximum cap
        assertEquals("5000 RR", rankService.getTierName(5000));
    }

    @Test
    public void testCalculatePointsToNextRank() {
        // Test standard calculation values
        assertEquals(66, rankService.getPointsToNextRank(234));
        assertEquals(1, rankService.getPointsToNextRank(199));

        // Test the maximum cap
        assertEquals(0, rankService.getPointsToNextRank(1500));
        assertEquals(0, rankService.getPointsToNextRank(2250));
    }
}