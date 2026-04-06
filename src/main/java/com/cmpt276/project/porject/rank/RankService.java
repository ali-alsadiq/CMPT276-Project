package com.cmpt276.project.porject.rank;

import org.springframework.stereotype.Service;
import com.cmpt276.project.porject.auth.User;

@Service
public class RankService {

    private static final int MAX_RR = 2000;
    private static final int TOTAL_RANKS = 10;
    private static final int RR_PER_RANK = MAX_RR / TOTAL_RANKS; // 100

    /**
     * Calculates the tier based on raw rr.
     * 
     * - Uses array indexing for cleaner and faster performance.
     * (Not sure if it makes a significant difference, can switch to if/else if
     * needed)
     * 
     * @param rr The raw rr to calculate the tier from.
     * @return The rank string based on the raw rr.
     */
    public String getTierName(int rr) {
        if (rr >= MAX_RR) {
            return String.valueOf(rr) + " RR";
        }

        String[] ranks = {
                "Bronze I", "Bronze II",
                "Silver I", "Silver II",
                "Gold I", "Gold II",
                "Platinum I", "Platinum II",
                "Diamond I", "Diamond II",
        };

        int index = rr / RR_PER_RANK;
        if (index >= ranks.length) {
            return ranks[ranks.length - 1];
        }

        return ranks[index];
    }

    // Returns true if the user has reached the max rank
    public boolean isMaxRank(int rr) {
        return rr >= MAX_RR;
    }

    // Returns the points needed to reach the next rank (for progress bar)
    // 100 - (rr % 100) = points needed to reach next rank
    public int getPointsToNextRank(int rr) {
        if (isMaxRank(rr)) {
            return 0;
        }
        return RR_PER_RANK - (rr % RR_PER_RANK);
    }

    // Returns the progress percentage for the progress bar
    public int getProgressPercentage(int rr) {
        if (isMaxRank(rr)) {
            return 100;
        }
        return rr % RR_PER_RANK;
    }

    /**
     * Returns the rank level from 1 to 10 based on RR.
     * 0-199   -> rank1
     * 200-399 -> rank2
     * ...
     * 1800+    -> rank10
     */
    private int getRankLevel(int rr) {
        int normalizedRr = Math.max(0, rr);

        if (normalizedRr >= MAX_RR) {
            return TOTAL_RANKS;
        }

        return (normalizedRr / RR_PER_RANK) + 1;
    }

    /**
     * Returns the full image path for the user's rank badge.
     * Example: /images/rank1.png ... /images/rank10.png
     */
    public String getRankImagePath(int rr) {
        int rankLevel = getRankLevel(rr);
        return "/images/rank" + rankLevel + ".png";
    }

    /**
     * Increases or Decreases the user's RR based on the provided amount
     * Use this method to update the user's RR when they complete a workout or meal
     * 
     * @param user                          The user to update the RR for
     * @param increaseAmount/decreaseAmount The amount to increase or decrease the
     *                                      RR by
     */
    public void increaseRR(User user, int increaseAmount) {
        int updated = user.getRankProfile().getRr() + increaseAmount;
        user.getRankProfile().setRr(updated);
    }

    public void decreaseRR(User user, int decreaseAmount) {
        int updated = user.getRankProfile().getRr() - decreaseAmount;
    user.getRankProfile().setRr(Math.max(0, updated));
    }    
}
