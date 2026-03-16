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
    /**
     * Increase or decrease a user's rr
     */
    public void increaseRR(User user, int increaseAmount) {
        user.getRankProfile().setRr(user.getRankProfile().getRr() + increaseAmount);
    }

    public void decreaseRR(User user, int decreaseAmount) {
        user.getRankProfile().setRr(user.getRankProfile().getRr() - decreaseAmount);
    }
}