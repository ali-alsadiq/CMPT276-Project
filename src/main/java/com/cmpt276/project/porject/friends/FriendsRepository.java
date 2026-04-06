package com.cmpt276.project.porject.friends;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cmpt276.project.porject.auth.User;

public interface FriendsRepository extends JpaRepository<Friends, Integer> {
    List<Friends> findByReceiverAndStatus(User receiver, String status);
    List<Friends> findBySenderAndStatus(User sender, String staus);
    Friends findBySenderAndReceiver(User sender, User Receiver);
    Friends findById(int id);
}
