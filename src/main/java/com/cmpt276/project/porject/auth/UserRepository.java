package com.cmpt276.project.porject.auth;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
    List<User> findAll();

    List<User> findByUsernameAndPassword(String username, String password);

    User findByUid(int uid);
}
