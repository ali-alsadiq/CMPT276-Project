package com.cmpt276.project.porject.rank;

import org.springframework.stereotype.Service;
import com.cmpt276.project.porject.auth.User;

@Service
public class RankService {
    public String getTierName(int rr) {
        if (rr >= 1500)
            return String.valueOf(rr) + " RR";

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

    public boolean isMaxRank(int rr) {
        return rr >= 1500;
    }

    public int getPointsToNextRank(int rr) {
        if (isMaxRank(rr)) {
            return 0;
        }
        return 100 - (rr % 100);
    }

    public int getProgressPercentage(int rr) {
        if (isMaxRank(rr)) {
            return 100;
        }
        return rr % 100;
    }

    public void increaseRR(User user, int increaseAmount) {
        user.getRankProfile().setRr(user.getRankProfile().getRr() + increaseAmount);
    }

    public void decreaseRR(User user, int decreaseAmount) {
        user.getRankProfile().setRr(user.getRankProfile().getRr() - decreaseAmount);
    }
}