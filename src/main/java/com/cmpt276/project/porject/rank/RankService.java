package com.cmpt276.project.porject.rank;

import org.springframework.stereotype.Service;
import com.cmpt276.project.porject.auth.User;

@Service
public class RankService {
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
        if (rr >= 1500) {
            return String.valueOf(rr) + " RR";
        }

        String[] ranks = {
                "Bronze I", "Bronze II", "Bronze III",
                "Silver I", "Silver II", "Silver III",
                "Gold I", "Gold II", "Gold III",
                "Platinum I", "Platinum II", "Platinum III",
                "Diamond I", "Diamond II", "Diamond III",
        };

        int index = rr / 100;
        if (index >= ranks.length) {
            return ranks[ranks.length - 1];
        }

        return ranks[index];
    }

    // Returns true if the user has reached the max rank
    public boolean isMaxRank(int rr) {
        return rr >= 1500;
    }

    // Returns the points needed to reach the next rank (for progress bar)
    // 100 - (rr % 100) = points needed to reach next rank
    public int getPointsToNextRank(int rr) {
        if (isMaxRank(rr)) {
            return 0;
        }
        return 100 - (rr % 100);
    }

    // Returns the progress percentage for the progress bar
    public int getProgressPercentage(int rr) {
        if (isMaxRank(rr)) {
            return 100;
        }
        return rr % 100;
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
        user.getRankProfile().setRr(updated);
    }
}