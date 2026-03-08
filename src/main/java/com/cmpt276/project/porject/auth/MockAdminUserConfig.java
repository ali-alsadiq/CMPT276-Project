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
                User admin = new User("System", "Admin", "admin_test", "admin", "ADMIN");
                admin.setRR(1685);

                User user1 = new User("System", "User", "user_test1", "user", "USER");
                User user2 = new User("System", "User", "user_test2", "user", "USER");

                List<User> mockUsers = Arrays.asList(admin, user1, user2);
                rankService.populateRanks(mockUsers);

                userRepository.saveAll(mockUsers);
            }

            catch (Exception e) {
                // Ignore if it already exists
            }
        };
    }
}
