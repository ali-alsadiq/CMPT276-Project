package com.cmpt276.project.porject;

import org.junit.jupiter.api.Test;
import com.cmpt276.project.porject.rank.RankProfile;

import static org.junit.jupiter.api.Assertions.*;

public class RankServiceTest {
    @Test
    public void testCalculateRank() {
        // Test standard rank values
        RankProfile profile = new RankProfile();
        
        profile.setRr(0);
        assertEquals("Bronze I", profile.getTierName());
        
        profile.setRr(99);
        assertEquals("Bronze I", profile.getTierName());

        // Test jumping into a new rank
        profile.setRr(100);
        assertEquals("Bronze II", profile.getTierName());
        
        profile.setRr(450);
        assertEquals("Silver II", profile.getTierName());

        // Test edge of last and next rank
        profile.setRr(899);
        assertEquals("Gold III", profile.getTierName());
        
        profile.setRr(900);
        assertEquals("Platinum I", profile.getTierName());

        // Test the maximum cap
        profile.setRr(5000);
        assertEquals("5000 RR", profile.getTierName());
    }

    @Test
    public void testCalculatePointsToNextRank() {
        // Test standard calculation values
        RankProfile profile = new RankProfile();
        
        profile.setRr(234);
        assertEquals(66, profile.getPointsToNextRank());
        
        profile.setRr(199);
        assertEquals(1, profile.getPointsToNextRank());

        // Test the maximum cap
        profile.setRr(1500);
        assertEquals(0, profile.getPointsToNextRank());
        
        profile.setRr(2250);
        assertEquals(0, profile.getPointsToNextRank());
    }
}