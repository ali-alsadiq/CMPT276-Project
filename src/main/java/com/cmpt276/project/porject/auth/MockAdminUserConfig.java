package com.cmpt276.project.porject.auth;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.Arrays;
import java.util.List;

/**
 * Configuration to add a mock admin user to the database on startup.
 * Easily removable before deploying to production.
 */
@Configuration
public class MockAdminUserConfig {

    @Bean
    public CommandLineRunner initMockAdminUser(UserRepository userRepository) {
        return args -> {
            try {
                User admin1 = new User("System", "Admin", "admin_test1", "admin", "ADMIN");
                admin1.getRankProfile().setRr(1685);

                User admin2 = new User("System", "Admin", "admin_test2", "admin", "ADMIN");
                admin2.getRankProfile().setRr(776);

                User user1 = new User("System", "User", "user_test1", "user", "USER");
                user1.getRankProfile().setRr(776);

                User user2 = new User("System", "User", "user_test2", "user", "USER");
                user2.getRankProfile().setRr(753);

                User user3 = new User("System", "User", "user_test3", "user", "USER");
                user3.getRankProfile().setRr(352);

                User user4 = new User("System", "User", "user_test4", "user", "USER");
                user4.getRankProfile().setRr(134);

                User user5 = new User("System", "User", "user_test5", "user", "USER");
                user5.getRankProfile().setRr(1573);

                User user6 = new User("System", "User", "user_test6", "user", "USER");
                user6.getRankProfile().setRr(863);

                List<User> mockUsers = Arrays.asList(admin1, admin2, user1, user2, user3, user4);

                userRepository.saveAll(mockUsers);
            }

            catch (Exception e) {
                // Ignore if it already exists
            }
        };
    }
}
