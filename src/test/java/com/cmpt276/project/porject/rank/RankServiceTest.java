package com.cmpt276.project.porject.rank;

import org.junit.jupiter.api.Test;

import com.cmpt276.project.porject.auth.User;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;

public class RankServiceTest {

    private RankService rankService;

    @BeforeEach
    public void setUp() {
        rankService = new RankService();
    }

    @Test
    public void testCalculateRank() {
        assertEquals("Bronze I", rankService.getTierName(0));
        assertEquals("Bronze I", rankService.getTierName(199));

        assertEquals("Bronze II", rankService.getTierName(200));
        assertEquals("Bronze II", rankService.getTierName(399));

        assertEquals("Silver I", rankService.getTierName(400));
        assertEquals("Silver II", rankService.getTierName(600));

        assertEquals("Gold I", rankService.getTierName(800));
        assertEquals("Gold II", rankService.getTierName(1000));

        assertEquals("Platinum I", rankService.getTierName(1200));
        assertEquals("Platinum II", rankService.getTierName(1400));

        assertEquals("Diamond I", rankService.getTierName(1600));
        assertEquals("Diamond II", rankService.getTierName(1800));
        
        // Test highest score
        assertEquals("2000 RR", rankService.getTierName(2000));
        assertEquals("2200 RR", rankService.getTierName(2200));
    }

    @Test
    public void isMaxRank() {
        assertFalse(rankService.isMaxRank(1999));
        assertTrue(rankService.isMaxRank(2000));
        assertTrue(rankService.isMaxRank(2500));
    }

    @Test
    public void testCalculatePointsToNextRank() {
        assertEquals(200, rankService.getPointsToNextRank(0));
        assertEquals(199, rankService.getPointsToNextRank(1));
        assertEquals(100, rankService.getPointsToNextRank(100));
        assertEquals(1, rankService.getPointsToNextRank(199));
        assertEquals(200, rankService.getPointsToNextRank(200));
        assertEquals(50, rankService.getPointsToNextRank(350));
        assertEquals(0, rankService.getPointsToNextRank(2000));
    }

    @Test
    public void getProgressPercentage_shouldReturnProgressInsideCurrent200PointBucket() {
        assertEquals(0, rankService.getProgressPercentage(0));
        assertEquals(50, rankService.getProgressPercentage(50));
        assertEquals(199, rankService.getProgressPercentage(199));
        assertEquals(0, rankService.getProgressPercentage(200));
        assertEquals(75, rankService.getProgressPercentage(275));
        assertEquals(100, rankService.getProgressPercentage(2000));
    }

    @Test
    public void getRankImagePath() {
        assertEquals("/images/rank1.png", rankService.getRankImagePath(0));
        assertEquals("/images/rank1.png", rankService.getRankImagePath(199));

        assertEquals("/images/rank2.png", rankService.getRankImagePath(200));
        assertEquals("/images/rank2.png", rankService.getRankImagePath(399));

        assertEquals("/images/rank3.png", rankService.getRankImagePath(400));
        assertEquals("/images/rank5.png", rankService.getRankImagePath(800));
        assertEquals("/images/rank10.png", rankService.getRankImagePath(1800));
        assertEquals("/images/rank10.png", rankService.getRankImagePath(2000));
    }

    @Test
    public void increaseRR() {
        User user = new User("Ali", "Test", "rank_test_user1", "pass", "USER");
        user.getRankProfile().setRr(100);

        rankService.increaseRR(user, 50);

        assertEquals(150, user.getRankProfile().getRr());
    }

    @Test
    public void decreaseRR() {
        User user = new User("Ali", "Test", "rank_test_user2", "pass", "USER");
        user.getRankProfile().setRr(100);

        rankService.decreaseRR(user, 30);

        assertEquals(70, user.getRankProfile().getRr());
    }
}