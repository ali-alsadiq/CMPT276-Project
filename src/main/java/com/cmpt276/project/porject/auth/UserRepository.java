package com.cmpt276.project.porject.auth;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Database access layer for the User entity.
 * 
 * FOR BACKEND IMPLEMENTATION:
 * - You can @Autowired this interface into your own controllers or services
 * if you need to look up a user's information using their 'uid'.
 */
public interface UserRepository extends JpaRepository<User, Integer> {
    List<User> findAll();

    List<User> findByUsername(String username);

    List<User> findByUsernameAndPassword(String username, String password);
    
    List<User> findAllByOrderByRankProfileRrDesc();

    List<User> findByUsernameContainingIgnoreCase(String username);

    User findByUid(int uid);
}
