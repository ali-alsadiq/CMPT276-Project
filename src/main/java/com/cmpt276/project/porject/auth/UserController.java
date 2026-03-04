package com.cmpt276.project.porject.auth;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/users/view")
    public String getAllUsers(Model model) {
        List<User> users = userRepository.findAll();
        model.addAttribute("users", users);

        return "users/showAll";
    }

    @GetMapping("/")
    public RedirectView process() {
        return new RedirectView("login");
    }

    @PostMapping("/users/add")
    public String addUser(@RequestParam Map<String, String> newUser, HttpServletResponse response) {
        String username = newUser.get("username");
        String password = newUser.get("password");
        String role = newUser.get("role");

        User user = new User(username, password, role);

        userRepository.save(user);

        return "redirect:/users/view";
    }

    @GetMapping("/login")
    public String getLoginModel(Model model, HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("session_user");
        if (user != null) {
            return "redirect:/";
        }

        else {
            model.addAttribute("user", user);
            return "login";
        }
    }

    @GetMapping("/register")
    public String getRegisterModel(Model model, HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("session_user");
        if (user != null) {
            return "redirect:/";
        }

        return "register";
    }

    @PostMapping("/login")
    public String login(@RequestParam Map<String, String> login, HttpServletRequest request,
            HttpServletResponse response) {
        String username = login.get("username");
        String password = login.get("password");

        List<User> users = userRepository.findByUsernameAndPassword(username, password);

        if (users.isEmpty()) {
            return "users/login";
        }

        else {
            User user = users.get(0);
            request.getSession().setAttribute("session_user", user);
            return "users/protected";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        request.getSession().invalidate();
        return "users/login";
    }
}
