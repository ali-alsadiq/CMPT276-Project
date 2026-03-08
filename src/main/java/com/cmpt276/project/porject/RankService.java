package com.cmpt276.project.porject;

import org.springframework.stereotype.Service;
import com.cmpt276.project.porject.auth.User;

import java.util.List;

@Service
public class RankService {

    /**
     * Calculates the tier based on raw XP.
     */
    public String calculateTier(int xp) {
        if (xp < 100)
            return "Bronze I";
        if (xp < 200)
            return "Bronze II";
        if (xp < 300)
            return "Bronze III";

        if (xp < 400)
            return "Silver I";
        if (xp < 500)
            return "Silver II";
        if (xp < 600)
            return "Silver III";

        if (xp < 700)
            return "Gold I";
        if (xp < 800)
            return "Gold II";
        if (xp < 900)
            return "Gold III";

        return "Ascendant";
    }

    public void populateRanks(List<User> users) {
        for (User user : users) {
            String calculatedRank = calculateTier(user.getRR());
            user.setRank(calculatedRank); // Saves it to temporary memory
        }
    }
}