package com.cmpt276.project.porject.auth;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    /**
     * Tests that findAll returns all users in the database.
     */
    @Test
    public void testFindAllUsers() {
        User testAdmin = new User("testAdmin", "pass123", "ADMIN");
        User testUser = new User("testUser", "pass123", "USER");
        userRepository.save(testAdmin);
        userRepository.save(testUser);

        List<User> allUsers = userRepository.findAll();

        assertEquals(2, allUsers.size());
    }

    /**
     * Tests that findByUsernameAndPassword returns the correct user.
     */
    @Test
    public void testFindByUsernameAndPassword() {
        User testUser = new User("testUser", "pass123", "USER");
        userRepository.save(testUser);

        List<User> foundUser = userRepository.findByUsernameAndPassword("testUser", "pass123");

        assertFalse(foundUser.isEmpty());
        assertEquals("testUser", foundUser.get(0).getUsername());
        assertEquals("pass123", foundUser.get(0).getPassword());
        assertEquals("USER", foundUser.get(0).getRole());
    }

    /**
     * Tests that findByUid returns the correct user.
     * 
     * - Uses a non-existent uid to test that it returns null.
     */
    @Test
    public void testFindByUid() {
        User user = new User("testUser", "pass123", "USER");
        userRepository.save(user);

        int userUid = user.getUid();

        User foundUser = userRepository.findByUid(userUid);
        User falseUser = userRepository.findByUid(userUid + 999);

        assertEquals(user, foundUser);
        assertNull(falseUser);
    }
}
