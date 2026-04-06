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
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class FriendController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FriendsRepository friendsRepository;

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

            // remove yourself
            List<User> filtered = results.stream()
                    .filter(u -> u.getUid() != user.getUid())
                    .filter(u -> !hasExistingFriends(user, u))
                    .collect(Collectors.toList());

            model.addAttribute("searchResults", filtered);
            model.addAttribute("searchQuery", search);
        }
        return "users/addFriends";
    }

    private boolean hasExistingFriends(User user1, User user2) {
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

    @GetMapping("/searchFriends")
    public List<User> searchFriends(@RequestParam("search") String search, HttpServletRequest request) {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("session_user");

        if (user == null) {
            return new ArrayList<>();
        }

        List<Friends> acceptedFriends = friendsRepository.findByReceiverAndStatus(user, "FRIENDS");
        List<Friends> sentFriends = friendsRepository.findBySenderAndStatus(user, "FRIENDS");

        List<User> friends = new ArrayList<>();
        for (Friends f : acceptedFriends) {
            friends.add(f.getReceiver());
        }
        for (Friends f : sentFriends) {
            friends.add(f.getReceiver());
        }

        List<User> filtered = friends.stream()
                .filter(friend -> friend.getUsername().toLowerCase().contains(search.toLowerCase())
                        || friend.getFirstname().toLowerCase().contains(search.toLowerCase())
                        || friend.getLastname().toLowerCase().contains(search.toLowerCase()))
                .collect(Collectors.toList());

        return filtered;
    }

}
