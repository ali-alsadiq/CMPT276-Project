package com.cmpt276.project.porject.friends;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import com.cmpt276.project.porject.auth.Friends;
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
    String addFriend(Model model, HttpServletRequest request, @RequestParam(value = "search", required = false) String search) {

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
        if (existing1 != null && (existing1.getStatus().equals("WAITING") || existing1.getStatus().equals("FRIENDS"))) {
            return true;
        }
        
        Friends existing2 = friendsRepository.findBySenderAndReceiver(user2, user1);
        if (existing2 != null && (existing2.getStatus().equals("WAITING") || existing2.getStatus().equals("FRIENDS"))) {
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

        //find targert
        User fTarget = userRepository.findByUid(friendId);

        //Check valid
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
        
        // Get pending friend requests
        List<Friends> pendingRequests = friendsRepository.findByReceiverAndStatus(user, "WAITING");
        model.addAttribute("pendingRequests", pendingRequests);
        model.addAttribute("pendingRequestCount", pendingRequests.size());
        
        return "inbox";
    }
    
}
