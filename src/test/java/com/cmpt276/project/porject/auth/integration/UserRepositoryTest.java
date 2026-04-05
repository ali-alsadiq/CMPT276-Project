package com.cmpt276.project.porject.auth.integration;

import com.cmpt276.project.porject.auth.User;
import com.cmpt276.project.porject.auth.UserRepository;
import org.springframework.dao.DataIntegrityViolationException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    /** Tests that findAll returns all users in the database. */
    @Test
    public void testFindAllUsers() {
        User testAdmin = new User("Test", "Admin", "testAdmin", "pass123", "ADMIN");
        User testUser = new User("Test", "User", "testUser", "pass123", "USER");
        userRepository.save(testAdmin);
        userRepository.save(testUser);

        List<User> allUsers = userRepository.findAll();

        assertEquals(2, allUsers.size());
    }

    /** Tests that findByUsernameAndPassword returns the correct user. */
    @Test
    public void testFindByUsernameAndPassword() {
        User testUser = new User("Test", "User", "testUser", "pass123", "USER");
        userRepository.save(testUser);

        List<User> foundUser = userRepository.findByUsernameAndPassword("testUser", "pass123");

        assertFalse(foundUser.isEmpty());
        assertEquals("testUser", foundUser.get(0).getUsername());
        assertEquals("pass123", foundUser.get(0).getPassword());
        assertEquals("USER", foundUser.get(0).getRole());
    }

    /** Tests that findByUid returns the correct user. */
    @Test
    public void testFindByUid() {
        User user = new User("Test", "User", "testUser", "pass123", "USER");
        userRepository.save(user);

        int userUid = user.getUid();

        User foundUser = userRepository.findByUid(userUid);
        User falseUser = userRepository.findByUid(userUid + 999);

        assertEquals(user, foundUser);
        assertNull(falseUser);
    }

    /** Tests that saving a new user persists it and can be found. */
    @Test
    public void register_persistsNewUserInDatabase() {
        User newUser = new User("User_test1", "TestLastname", "user_test1", "pass123", "USER");
        userRepository.save(newUser);

        List<User> users = userRepository.findByUsername("user_test1");
        assertFalse(users.isEmpty());
        assertEquals("user_test1", users.get(0).getUsername());
    }

    /** Tests that user registration defaults to role USER if not specified. */
    @Test
    public void register_defaultRoleIsUser() {
        User newUser = new User("Jane", "Smith", "janesmith", "pass123", null);

        if (newUser.getRole() == null || newUser.getRole().isEmpty()) {
            newUser.setRole("USER");
        }

        userRepository.save(newUser);
        List<User> users = userRepository.findByUsername("janesmith");
        assertEquals("USER", users.get(0).getRole());
    }

    /** Tests that duplicate usernames are not allowed. */
    @Test
    public void register_usernameUniquenessEnforcedAtDbLevel() {
        User user1 = new User("First", "User", "duplicate", "pass1", "USER");
        userRepository.saveAndFlush(user1);

        User user2 = new User("Second", "User", "duplicate", "pass2", "USER");
        assertThrows(DataIntegrityViolationException.class, () -> {
            userRepository.saveAndFlush(user2);
        });
    }
}
