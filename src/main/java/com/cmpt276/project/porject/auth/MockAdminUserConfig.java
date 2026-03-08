package com.cmpt276.project.porject.auth;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
                userRepository.save(new User("System", "Admin", "admin_test", "admin", "ADMIN"));
                userRepository.save(new User("System", "User", "user_test1", "user", "USER"));
                userRepository.save(new User("System", "User", "user_test2", "user", "USER"));
            }

            catch (Exception e) {
                // Ignore if it already exists
            }
        };
    }
}
