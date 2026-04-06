package com.cmpt276.project.porject.friends;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.cmpt276.project.porject.auth.User;
import com.cmpt276.project.porject.auth.UserRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class FriendController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FriendsRepository friendsRepository;

    /**
     * Add friends get mapping
     * @param model
     * @param request
     * @param search
     * @return
     */
    @GetMapping("/addFriends")
    String addFriend(Model model, HttpServletRequest request,
            @RequestParam(value = "search", required = false) String search) {

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("session_user");

        // validate logged in
        if (user == null) {
            return "redirect:/login";
        }

        if (search != null && !search.trim().isEmpty()) {
            List<User> results = userRepository.findByUsernameContainingIgnoreCase(search);

            // remove yourself, those friended
            List<User> filtered = results.stream()
                    .filter(u -> u.getUid() != user.getUid())
                    .filter(u -> !hasFriends(user, u))
                    .collect(Collectors.toList());

            model.addAttribute("searchResults", filtered);
            model.addAttribute("searchQuery", search);
        }
        return "users/addFriends";
    }

    /**
     * Helper method to check if user is friended or pending
     * @param user1
     * @param user2
     * @return
     */
    private boolean hasFriends(User user1, User user2) {
        Friends existing1 = friendsRepository.findBySenderAndReceiver(user1, user2);
        if (existing1 != null && (existing1.getStatus().equals("PENDING") || existing1.getStatus().equals("FRIENDS"))) {
            return true;
        }

        Friends existing2 = friendsRepository.findBySenderAndReceiver(user2, user1);
        if (existing2 != null && (existing2.getStatus().equals("PENDING") || existing2.getStatus().equals("FRIENDS"))) {
            return true;
        }

        return false;
    }

    /**
     * Post for friend requests
     * @param friendId
     * @param request
     * @param model
     * @return
     */
    @PostMapping("/sendFriendRequest")
    public String sendFriendRequest(@RequestParam("friendId") int friendId, HttpServletRequest request, Model model) {

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("session_user");

        if (user == null) {
            return "redirect:/login";
        }

        // find targert
        User fTarget = userRepository.findByUid(friendId);

        // Check valid
        if (fTarget == null) {
            model.addAttribute("error", "User not found.");
            return "redirect:/addFriends";
        }

        Friends friendRequest = new Friends(user, fTarget);
        friendsRepository.save(friendRequest);

        model.addAttribute("success", "Friend request sent to " + fTarget.getUsername());
        return "redirect:/addFriends?search=" + fTarget.getUsername();
    }

    /**
     * Get mapping for inbox
     * @param model
     * @param request
     * @return
     */
    @GetMapping("/inbox")
    public String getInbox(Model model, HttpServletRequest request) {

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("session_user");

        if (user == null) {
            return "redirect:/login";
        }

        List<Friends> pendingRequests = friendsRepository.findByReceiverAndStatus(user, "PENDING");

        model.addAttribute("pendingRequests", pendingRequests);

        model.addAttribute("pendingRequestCount", pendingRequests.size());

        return "inbox";
    }

    /**
     * Post for accepting friend requests
     * @param requestId
     * @param request
     * @param model
     * @return
     */
    @PostMapping("/acceptFriendRequest")
    public String acceptFriendRequest(@RequestParam("requestId") int requestId, HttpServletRequest request,
            Model model) {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("session_user");

        if (user == null) {
            return "redirect:/login";
        }

        Friends fRequest = friendsRepository.findById(requestId);

        if (fRequest == null) {
            model.addAttribute("error", "There was an issue accepting the request.");
            return "redirect:/inbox";
        }

        fRequest.setStatus("FRIENDS");
        friendsRepository.save(fRequest);

        model.addAttribute("success", "You are now friends with " + fRequest.getSender().getUsername() + "!");

        return "redirect:/inbox";
    }

    /**
     * Post for rejecting friend requests
     * @param requestId
     * @param request
     * @param model
     * @return
     */
    @PostMapping("/rejectFriendRequest")
    public String rejectFriendRequest(@RequestParam("requestId") int requestId, HttpServletRequest request,
            Model model) {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("session_user");

        if (user == null) {
            return "redirect:/login";
        }

        Friends fRequest = friendsRepository.findById(requestId);

        if (fRequest == null) {
            model.addAttribute("error", "There was an issue accepting the request.");
            return "redirect:/inbox";
        }

        String senderUsername = fRequest.getSender().getUsername();
        friendsRepository.delete(fRequest);
        model.addAttribute("success", "Friend request from " + senderUsername + " has been rejected.");

        return "redirect:/inbox";
    }

    /**
     * Post for removing friends
     * 
     * @param friendId
     * @param request
     * @param model
     * @return
     */
    @PostMapping("/removeFriend")
    public String removeFriend(@RequestParam("friendId") int friendId, HttpServletRequest request, Model model) {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("session_user");

        if (user == null) {
            return "redirect:/login";
        }

        User removeTarget = userRepository.findByUid(friendId);

        if (removeTarget == null) {
            model.addAttribute("error", "Friend not found.");
            return "redirect:/profile";
        }

        Friends sent = friendsRepository.findBySenderAndReceiver(user, removeTarget);
        Friends recieved = friendsRepository.findBySenderAndReceiver(removeTarget, user);
        
        if (sent != null) {
            friendsRepository.delete(sent);
        } else if (recieved != null) {
            friendsRepository.delete(recieved);
        }

        model.addAttribute("success", "Removed " + removeTarget.getUsername() + " from your friends list.");
    
        return "redirect:/profile";

    }

}
