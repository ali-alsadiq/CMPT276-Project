package com.cmpt276.project.porject.auth;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FriendsRepository extends JpaRepository<Friends, Integer> {
    List<Friends> findByReceiverAndStatus(User receiver, String status);
    List<Friends> findBySenderOrReceiverAndStatus(User sender, User receiver, String status);
    Friends findBySenderAndReceiver(User sender, User Receiver);
}
