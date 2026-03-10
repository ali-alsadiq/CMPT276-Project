package com.cmpt276.project.porject;

import org.springframework.stereotype.Service;
import com.cmpt276.project.porject.auth.User;

import java.util.List;

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
    public String calculateRank(int rr) {
        // If rr is greater than or equal to 1500, return the rr as a string
        if (rr >= 1500)
            return String.valueOf(rr) + " RR";

        String[] ranks = {
                "Bronze I", "Bronze II", "Bronze III", // 0 - 300rr
                "Silver I", "Silver II", "Silver III", // 301 - 600rr
                "Gold I", "Gold II", "Gold III", // 601 - 900rr
                "Platinum I", "Platinum II", "Platinum III", // 901 - 1200rr
                "Diamond I", "Diamond II", "Diamond III", // 1201 - 1500rr
        };

        // Divide by 100 to get the exact array index
        // Example: 250 / 100 = 2 ("Bronze III")
        // Example: 410 / 100 = 4 ("Silver II")
        int index = rr / 100;

        return ranks[index];
    }

    /**
     * Increase or decrease a user's rr
     */
    public void increaseRR(User user, int increaseAmount) {
        user.setRR(user.getRR() + increaseAmount);
    }

    public void decreaseRR(User user, int decreaseAmount) {
        user.setRR(user.getRR() - decreaseAmount);
    }

    /**
     * Calculates how much rr the user needs to reach the next tier.
     * 
     * - Mainly used for progress bar in dashboard and nav bar
     */
    public int calculatePointsToNextRank(int rr) {
        // If the user is already at the maximum rank
        if (rr >= 1500) {
            return 0;
        }

        // Find their progress within their current 100-point rank tier
        int progressInCurrentRank = rr % 100;

        // Subtract that from 100 to get the remaining points needed
        return 100 - progressInCurrentRank;
    }

    /**
     * Populates the ranks of all users in the list.
     * 
     * @param users The list of users to populate the ranks of.
     */
    public void populateRanks(List<User> users) {
        for (User user : users) {
            String calculatedRank = calculateRank(user.getRR());

            user.setRank(calculatedRank); // Saves it to temporary memory
        }
    }
}