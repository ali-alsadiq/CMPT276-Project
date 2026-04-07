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
        // Formula: (rr % 200) * 100 / 200  (integer division)
        assertEquals(0,   rankService.getProgressPercentage(0));    // 0/200 = 0%
        assertEquals(25,  rankService.getProgressPercentage(50));   // 50*100/200 = 25%
        assertEquals(99,  rankService.getProgressPercentage(199));  // 199*100/200 = 99%
        assertEquals(0,   rankService.getProgressPercentage(200));  // resets at new tier
        assertEquals(37,  rankService.getProgressPercentage(275));  // 75*100/200 = 37%
        assertEquals(100, rankService.getProgressPercentage(2000)); // max rank = 100%
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

    // =========================================================
    //              ADDITIONAL EDGE-CASE TESTS
    // =========================================================

    // --- getTierName ---

    @Test
    public void getTierName_exactBoundaries_correctTier() {
        // Every tier boundary: first RR of each tier
        assertEquals("Bronze I",    rankService.getTierName(0));
        assertEquals("Bronze II",   rankService.getTierName(200));
        assertEquals("Silver I",    rankService.getTierName(400));
        assertEquals("Silver II",   rankService.getTierName(600));
        assertEquals("Gold I",      rankService.getTierName(800));
        assertEquals("Gold II",     rankService.getTierName(1000));
        assertEquals("Platinum I",  rankService.getTierName(1200));
        assertEquals("Platinum II", rankService.getTierName(1400));
        assertEquals("Diamond I",   rankService.getTierName(1600));
        assertEquals("Diamond II",  rankService.getTierName(1800));
    }

    @Test
    public void getTierName_oneBeforeBoundary_staysInLowerTier() {
        // One point below each boundary stays in the previous tier
        assertEquals("Bronze I",    rankService.getTierName(199));
        assertEquals("Bronze II",   rankService.getTierName(399));
        assertEquals("Silver I",    rankService.getTierName(599));
        assertEquals("Silver II",   rankService.getTierName(799));
        assertEquals("Gold I",      rankService.getTierName(999));
        assertEquals("Gold II",     rankService.getTierName(1199));
        assertEquals("Platinum I",  rankService.getTierName(1399));
        assertEquals("Platinum II", rankService.getTierName(1599));
        assertEquals("Diamond I",   rankService.getTierName(1799));
        assertEquals("Diamond II",  rankService.getTierName(1999));
    }

    @Test
    public void getTierName_aboveMax_returnsRrString() {
        // Any value >= 2000 should return "<rr> RR"
        assertEquals("2000 RR", rankService.getTierName(2000));
        assertEquals("2001 RR", rankService.getTierName(2001));
        assertEquals("9999 RR", rankService.getTierName(9999));
    }

    // --- isMaxRank ---

    @Test
    public void isMaxRank_justBelowMax_returnsFalse() {
        assertFalse(rankService.isMaxRank(1999));
    }

    @Test
    public void isMaxRank_exactlyMax_returnsTrue() {
        assertTrue(rankService.isMaxRank(2000));
    }

    @Test
    public void isMaxRank_zero_returnsFalse() {
        assertFalse(rankService.isMaxRank(0));
    }

    // --- getPointsToNextRank ---

    @Test
    public void getPointsToNextRank_atMaxRank_returnsZero() {
        assertEquals(0, rankService.getPointsToNextRank(2000));
        assertEquals(0, rankService.getPointsToNextRank(2500));
    }

    @Test
    public void getPointsToNextRank_exactlyAtTierStart_returnsFull200() {
        // 400 is the start of Silver I; 200 points to the next tier
        assertEquals(200, rankService.getPointsToNextRank(400));
        assertEquals(200, rankService.getPointsToNextRank(1200));
    }

    @Test
    public void getPointsToNextRank_onePointBeforeTier_returnsOne() {
        assertEquals(1, rankService.getPointsToNextRank(199));
        assertEquals(1, rankService.getPointsToNextRank(399));
        assertEquals(1, rankService.getPointsToNextRank(1999));
    }

    // --- getProgressPercentage ---

    @Test
    public void getProgressPercentage_exactlyAtTierStart_returnsZero() {
        assertEquals(0, rankService.getProgressPercentage(0));
        assertEquals(0, rankService.getProgressPercentage(200));
        assertEquals(0, rankService.getProgressPercentage(400));
    }

    @Test
    public void getProgressPercentage_halfwayThroughTier_returns50() {
        // 100/200 = 50%
        assertEquals(50, rankService.getProgressPercentage(100));
        assertEquals(50, rankService.getProgressPercentage(300));
        assertEquals(50, rankService.getProgressPercentage(1500));
    }

    @Test
    public void getProgressPercentage_atOrAboveMax_returns100() {
        assertEquals(100, rankService.getProgressPercentage(2000));
        assertEquals(100, rankService.getProgressPercentage(3000));
    }

    // --- getRankImagePath ---

    @Test
    public void getRankImagePath_allTenLevels_correctImage() {
        // rank level = (rr / 200) + 1, capped at 10
        String[] expected = {
            "/images/rank1.png",  // 0
            "/images/rank2.png",  // 200
            "/images/rank3.png",  // 400
            "/images/rank4.png",  // 600
            "/images/rank5.png",  // 800
            "/images/rank6.png",  // 1000
            "/images/rank7.png",  // 1200
            "/images/rank8.png",  // 1400
            "/images/rank9.png",  // 1600
            "/images/rank10.png", // 1800
        };
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], rankService.getRankImagePath(i * 200),
                    "Unexpected image for rr=" + (i * 200));
        }
    }

    @Test
    public void getRankImagePath_aboveMax_stillRank10() {
        assertEquals("/images/rank10.png", rankService.getRankImagePath(2000));
        assertEquals("/images/rank10.png", rankService.getRankImagePath(9999));
    }

    // --- increaseRR / decreaseRR ---

    @Test
    public void increaseRR_fromZero_updatesCorrectly() {
        User user = new User("A", "B", "rr_inc_zero", "pass", "USER");
        rankService.increaseRR(user, 75);
        assertEquals(75, user.getRankProfile().getRr());
    }

    @Test
    public void increaseRR_pastMaxRank_noClampApplied() {
        // RankService.increaseRR has no upper cap — this documents the current behaviour
        User user = new User("A", "B", "rr_inc_past_max", "pass", "USER");
        user.getRankProfile().setRr(1990);
        rankService.increaseRR(user, 100);
        // Expected: 2090 (no cap in increaseRR)
        assertEquals(2090, user.getRankProfile().getRr());
    }

    @Test
    public void decreaseRR_belowZero_isFlooredAtZero() {
        User user = new User("A", "B", "rr_dec_floor", "pass", "USER");
        user.getRankProfile().setRr(10);
        rankService.decreaseRR(user, 999);
        assertEquals(0, user.getRankProfile().getRr());
    }

    @Test
    public void decreaseRR_exactlyToZero_isZero() {
        User user = new User("A", "B", "rr_dec_exact_zero", "pass", "USER");
        user.getRankProfile().setRr(50);
        rankService.decreaseRR(user, 50);
        assertEquals(0, user.getRankProfile().getRr());
    }

    @Test
    public void decreaseRR_byZero_doesNotChange() {
        User user = new User("A", "B", "rr_dec_by_zero", "pass", "USER");
        user.getRankProfile().setRr(200);
        rankService.decreaseRR(user, 0);
        assertEquals(200, user.getRankProfile().getRr());
    }

    @Test
    public void increaseRR_byZero_doesNotChange() {
        User user = new User("A", "B", "rr_inc_by_zero", "pass", "USER");
        user.getRankProfile().setRr(300);
        rankService.increaseRR(user, 0);
        assertEquals(300, user.getRankProfile().getRr());
    }
}