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

/**
 * Controller for handling user authentication and user management.
 * 
 * FOR BACKEND IMPLEMENTATION:
 * - To check if a user is loggin in, on your own controller, use:
 * User user = (User) request.getSession().getAttribute("session_user");
 */
@Controller
public class UserController {
    @Autowired
    private UserRepository userRepository;

    /**
     * Admin Dashboard, shows list of all users.
     * 
     * - Only accessible by users with "ADMIN" role.
     */
    @GetMapping("/users/view")
    public String getAllUsers(Model model, HttpServletRequest request) {
        // Check if user is logged in has "ADMIN" role
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("session_user");

        if (user == null || !"ADMIN".equals(user.getRole())) {
            return "redirect:/login";
        }

        // If user is not logged in or not admin, redirect to login
        if (user == null || !user.getRole().equals("ADMIN")) {
            return "redirect:/login";
        }

        List<User> users = userRepository.findAll();
        model.addAttribute("users", users);

        return "users/adminDashboard"; // TODO: Create this page
    }

    @GetMapping("/")
    public RedirectView process() {
        return new RedirectView("login");
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam Map<String, String> newUser, Model model, HttpServletResponse response) {
        boolean hasError = validateFields(newUser, model, "firstname", "lastname", "username", "password");

        if (hasError) {
            model.addAttribute("error", "Fill all required fields.");
            return "register";
        }

        String username = newUser.get("username");
        String password = newUser.get("password");

        if (calculatePasswordStrength(password) <= 2) {
            model.addAttribute("passwordError", true);
            model.addAttribute("error", "Password is too weak.");
            return "register";
        }

        String role = newUser.getOrDefault("role", "USER");

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
    public String login(@RequestParam Map<String, String> login, Model model, HttpServletRequest request,
            HttpServletResponse response) {
        boolean hasError = validateFields(login, model, "username", "password");

        if (hasError) {
            model.addAttribute("error", "Fill all required fields.");
            return "login";
        }

        String username = login.get("username");
        String password = login.get("password");

        List<User> users = userRepository.findByUsernameAndPassword(username, password);

        if (users.isEmpty()) {
            model.addAttribute("error", "The username or password you entered is incorrect.");
            model.addAttribute("usernameVal", username);
            return "login";
        }

        else {
            User user = users.get(0);
            request.getSession().setAttribute("session_user", user);
            return "redirect:/users/view";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        request.getSession().invalidate();
        return "redirect:/login";
    }

    private boolean validateFields(Map<String, String> data, Model model, String... fieldNames) {
        boolean hasError = false;
        for (String fieldName : fieldNames) {
            String value = data.get(fieldName);
            if (value == null || value.trim().isEmpty()) {
                model.addAttribute(fieldName + "Error", true);
                hasError = true;
            }

            else {
                model.addAttribute(fieldName + "Val", value);
            }
        }
        return hasError;
    }

    private int calculatePasswordStrength(String password) {
        int score = 0;
        if (password == null || password.isEmpty()) {
            return score;
        }

        score++; // length > 0

        if (password.length() >= 8) {
            score++;
        }

        if (password.matches(".*[a-z].*") && password.matches(".*[A-Z].*")) {
            score++;
        }

        if (password.matches(".*[0-9].*")) {
            score++;
        }

        if (password.matches(".*[!@#$%^&*].*")) {
            score++;
        }

        return score;
    }
}