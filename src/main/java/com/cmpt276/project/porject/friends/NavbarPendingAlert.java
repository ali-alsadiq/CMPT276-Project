package com.cmpt276.project.porject.friends;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.ui.Model;
import com.cmpt276.project.porject.auth.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.List;

@ControllerAdvice
public class NavbarPendingAlert {
    
    @Autowired
    private FriendsRepository friendsRepository;
    
    @ModelAttribute
    public void addPendingRequestCount(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("session_user");
        
        if (user != null) {

            int pendingCount = 0;
            List<Friends> pending = friendsRepository.findByReceiverAndStatus(user, "PENDING");
            pendingCount = pending.size();

            model.addAttribute("pendingRequestCount", pendingCount);
        } else {
            model.addAttribute("pendingRequestCount", 0);
        }
    }
}