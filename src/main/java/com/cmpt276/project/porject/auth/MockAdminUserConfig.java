package com.cmpt276.project.porject.auth;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.cmpt276.project.porject.RankService;
import java.util.Arrays;
import java.util.List;

/**
 * Configuration to add a mock admin user to the database on startup.
 * Easily removable before deploying to production.
 */
@Configuration
public class MockAdminUserConfig {

    @Bean
    public CommandLineRunner initMockAdminUser(UserRepository userRepository, RankService rankService) {
        return args -> {
            try {
                User admin1 = new User("System", "Admin", "admin_test1", "admin", "ADMIN");
                admin1.getRankProfile().setRr(1685);

                User admin2 = new User("System", "Admin", "admin_test2", "admin", "ADMIN");
                admin2.getRankProfile().setRr(776);

                User user1 = new User("System", "User", "user_test1", "user", "USER");
                User user2 = new User("System", "User", "user_test2", "user", "USER");

                List<User> mockUsers = Arrays.asList(admin1, admin2, user1, user2);
                // rankService.populateRanks(mockUsers); // Method no longer needed, rank dynamically calculates for templates 

                userRepository.saveAll(mockUsers);
            }

            catch (Exception e) {
                // Ignore if it already exists
            }
        };
    }
}
