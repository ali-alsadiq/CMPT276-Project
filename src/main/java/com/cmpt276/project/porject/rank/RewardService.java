package com.cmpt276.project.porject.rank;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cmpt276.project.porject.auth.User;
import com.cmpt276.project.porject.auth.UserRepository;

@Service
public class RewardService {

    @Autowired
    private RankService rankService;

    @Autowired
    private UserRepository userRepository;

    /**
     * Rewards the user with 10 RR upon logging a meal
     * 
     * @param user The user who logged the meal.
     */
    public void rewardForLoggingMeal(User user) {
        if (user != null) {
            rankService.increaseRR(user, 10);
            userRepository.save(user);
        }
    }
}
