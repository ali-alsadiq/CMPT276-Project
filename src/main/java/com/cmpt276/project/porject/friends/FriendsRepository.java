package com.cmpt276.project.porject.friends;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cmpt276.project.porject.auth.User;

public interface FriendsRepository extends JpaRepository<Friends, Integer> {
    List<Friends> findByReceiverAndStatus(User receiver, String status);
    List<Friends> findBySenderOrReceiverAndStatus(User sender, User receiver, String status);
    Friends findBySenderAndReceiver(User sender, User Receiver);
    Friends findById(int id);
}
