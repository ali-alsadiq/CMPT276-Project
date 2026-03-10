package com.cmpt276.project.porject.auth;

import com.cmpt276.project.porject.RankService;

import java.util.ArrayList;
import java.util.HashMap;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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

    @Autowired
    private RankService rankService;

    /**
     * Admin Dashboard, shows list of all users.
     * 
     * - Only accessible by users with "ADMIN" role.
     * 
     * @param model   Model to add attributes to.
     * @param request Request to get session from.
     * @return String representing the view to return.
     */
    @GetMapping("/adminDashboard")
    public String getAllUsers(Model model, HttpServletRequest request) {
        // Check if user is logged in has "ADMIN" role
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("session_user");

        // If user is not logged in or not admin, redirect to login
        if (user == null || !user.isAdmin()) {
            return "redirect:/login";
        }

        user.setRank(rankService.calculateRank(user.getRR()));

        List<User> users = userRepository.findAll();
        model.addAttribute("users", users);

        return "adminDashboard";
    }

    /**
     * Redirects to login page.
     * 
     * - Any "user" who is not logged in gets landed on login page.
     */
    @GetMapping("/")
    public RedirectView process() {
        return new RedirectView("login");
    }

    // -- Login --

    /**
     * Handles login requests.
     * 
     * - Checks if all required fields are filled.
     * - Checks if username and password are correct.
     * - Checks if user is logged in.
     * 
     * @param login    Map containing login data.
     * @param model    Model to add attributes to.
     * @param request  Request to get session from.
     * @param response Response to send to the client.
     * @return String representing the view to return.
     */
    @GetMapping("/login")
    public String getLoginModel(Model model, HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("session_user");

        // If user is logged in, redirect to dashboard
        if (user != null) {
            if (user.isAdmin()) {
                return "redirect:/adminDashboard";
            }

            else {
                return "redirect:/dashboard"; // doesn't exist yet
            }
        }

        model.addAttribute("user", user);
        return "users/login";
    }

    /**
     * Handles login requests.
     * 
     * @param login    Map containing login data.
     * @param model    Model to add attributes to.
     * @param request  Request to get session from.
     * @param response Response to send to the client.
     * @return String representing the view to return.
     */
    @PostMapping("/login")
    public String login(@RequestParam Map<String, String> login, Model model, HttpServletRequest request,
            HttpServletResponse response) {
        boolean hasError = validateFields(login, model, "username", "password");

        // If no username or password, return to login page
        if (hasError) {
            model.addAttribute("error", "Fill all required fields.");
            return "users/login";
        }

        String username = login.get("username").trim();
        String password = login.get("password");

        List<User> users = userRepository.findByUsernameAndPassword(username, password);

        // If no username or password, return to login page
        if (users.isEmpty()) {
            model.addAttribute("error", "The username or password you entered is incorrect.");
            model.addAttribute("usernameVal", username);
            return "users/login";
        }

        // If username and password are correct, log the user in
        else {
            User user = users.get(0);
            request.getSession().setAttribute("session_user", user);

            if (user.isAdmin()) {
                return "redirect:/adminDashboard"; // Redirect to admin dashboard endpoint
            } else {
                return "redirect:/dashboard"; // Redirect to nothing / home for now
            }
        }
    }

    // -- Register --

    /**
     * Handles user registration.
     * 
     * - Checks if all required fields are filled.
     * - Checks if password is at least 8 characters long.
     * - Checks if password contains at least one uppercase letter, one lowercase
     * letter, one number, and one special character.
     * - Checks if username is unique.
     * 
     * @param newUser  Map containing user registration data.
     * @param model    Model to add attributes to.
     * @param response Response to send to the client.
     * @return String representing the view to return.
     */
    @PostMapping("/register")
    public String registerUser(@RequestParam Map<String, String> newUser, Model model, HttpServletResponse response) {
        boolean hasError = validateFields(newUser, model, "firstname", "lastname", "username", "password");

        // If no username or password, return to register page
        if (hasError) {
            model.addAttribute("error", "Fill all required fields.");
            return "users/register";
        }

        String firstname = newUser.get("firstname").trim();
        String lastname = newUser.get("lastname").trim();
        String username = newUser.get("username").trim();
        String password = newUser.get("password");

        if (username.contains(" ")) {
            model.addAttribute("usernameError", true);
            model.addAttribute("error", "Username cannot contain spaces.");
            return "users/register";
        }

        if (!userRepository.findByUsername(username).isEmpty()) {
            model.addAttribute("usernameError", true);
            model.addAttribute("error", "Username already exists.");
            return "users/register";
        }

        // Check if password strength is sufficient (>= 3 is Fair)
        if (calculatePasswordStrength(password) < 3) {
            model.addAttribute("passwordError", true);
            model.addAttribute("error", "Password is too weak.");
            return "users/register";
        }

        String role = newUser.getOrDefault("role", "USER");
        User user = new User(firstname, lastname, username, password, role);
        userRepository.save(user);

        return "redirect:/login";
    }

    /**
     * Handles register requests.
     * 
     * @param model    Model to add attributes to.
     * @param request  Request to get session from.
     * @param response Response to send to the client.
     * @return String representing the view to return.
     */
    @GetMapping("/register")
    public String getRegisterModel(Model model, HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("session_user");

        // If user is logged in, redirect to dashboard
        if (user != null) {
            if (user.isAdmin()) {
                return "redirect:/adminDashboard"; // Redirect to admin dashboard endpoint
            } else {
                return "redirect:/dashboard"; // Redirect to nothing / home for now
            }
        }

        // If unsuccessful, return to register page
        return "users/register";
    }

    // -- Calorie Tracker --

    /**
     * Handles calorie tracker page requests.
     *
     * - Only accessible by logged-in users.
     *
     * @param request request used to retrieve the current session
     * @param model   model used to pass progress values to the view
     * @return the calorie tracker view, or redirect to login if user is not logged
     *         in
     */
    @GetMapping("/calorieTracker")
    public String getCalorieTracker(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("session_user");

        // If user is not logged in, redirect to login
        // if (user == null) {
        // return "redirect:/login";
        // }

        model.addAttribute("totalPercent", 67);
        model.addAttribute("proteinPercent", 82);
        model.addAttribute("carbsPercent", 74);
        model.addAttribute("macrosPercent", 91);

        return "calorieTracker";
    }

    @RestController
    @RequestMapping("/api")
    public class NutritionApiController {

        @GetMapping("/nutrition")
        public List<Map<String, Object>> getNutrition(@RequestParam String query) {

            List<Map<String, Object>> foods = new ArrayList<>();

            Map<String, Object> food1 = new HashMap<>();
            food1.put("name", "Prime Rib");
            food1.put("calories", 850);
            food1.put("fat_total_g", 65);
            food1.put("carbohydrates_total_g", 2);

            Map<String, Object> food2 = new HashMap<>();
            food2.put("name", "Mashed Potatoes");
            food2.put("calories", 240);
            food2.put("fat_total_g", 10);
            food2.put("carbohydrates_total_g", 35);

            foods.add(food1);
            foods.add(food2);

            return foods;
        }
    }

    // -- Logout --

    /**
     * Handles logout requests.
     * 
     * - Redirects to login page.
     * 
     * @param request Request to get session from.
     * @return String representing the view to return.
     */
    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        request.getSession().invalidate();
        return "redirect:/login";
    }

    /**
     * Displays the user profile page.
     * 
     * @param model   Model to add attributes to.
     * @param request Request to get session from.
     * @return String representing the view to return.
     */
    @GetMapping("/profile")
    public String getProfilePage(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("session_user");

        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute("user", user);
        return "users/profile";
    }

    /**
     * Updates the user profile information.
     * 
     * @param profileData Map containing profile data.
     * @param model       Model to add attributes to.
     * @param request     Request to get session from.
     * @return String representing the view to return.
     */
    @PostMapping("/profile")
    public String updateProfile(@RequestParam Map<String, String> profileData, Model model,
            HttpServletRequest request) {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("session_user");

        if (user == null) {
            return "redirect:/login";
        }

        boolean hasError = false;

        String firstname = profileData.get("firstname") != null ? profileData.get("firstname").trim() : "";
        String lastname = profileData.get("lastname") != null ? profileData.get("lastname").trim() : "";
        String sex = profileData.get("sex") != null ? profileData.get("sex").trim() : "";
        String dateOfBirthStr = profileData.get("dateOfBirth") != null ? profileData.get("dateOfBirth").trim() : "";
        String heightStr = profileData.get("height") != null ? profileData.get("height").trim() : "";
        String weightStr = profileData.get("weight") != null ? profileData.get("weight").trim() : "";
        String caloriesDailyGoalStr = profileData.get("caloriesDailyGoal") != null
                ? profileData.get("caloriesDailyGoal").trim()
                : "";

        model.addAttribute("firstnameVal", firstname);
        model.addAttribute("lastnameVal", lastname);
        model.addAttribute("sexVal", sex);
        model.addAttribute("dateOfBirthVal", dateOfBirthStr);
        model.addAttribute("heightVal", heightStr);
        model.addAttribute("weightVal", weightStr);
        model.addAttribute("caloriesDailyGoalVal", caloriesDailyGoalStr);

        if (firstname.isEmpty()) {
            model.addAttribute("firstnameError", true);
            hasError = true;
        }

        if (lastname.isEmpty()) {
            model.addAttribute("lastnameError", true);
            hasError = true;
        }

        if (sex.isEmpty()) {
            model.addAttribute("sexError", true);
            hasError = true;
        }

        LocalDate dateOfBirth = null;
        if (dateOfBirthStr.isEmpty()) {
            model.addAttribute("dateOfBirthError", true);
            hasError = true;
        } else {
            try {
                dateOfBirth = LocalDate.parse(dateOfBirthStr);

                if (!dateOfBirth.isBefore(LocalDate.now())) {
                    model.addAttribute("dateOfBirthError", true);
                    model.addAttribute("error", "Date of birth must be in the past.");
                    hasError = true;
                }
            } catch (Exception e) {
                model.addAttribute("dateOfBirthError", true);
                model.addAttribute("error", "Please enter a valid date of birth.");
                hasError = true;
            }
        }

        double height = 0;
        if (heightStr.isEmpty()) {
            model.addAttribute("heightError", true);
            hasError = true;
        } else {
            try {
                height = Double.parseDouble(heightStr);

                if (height < 50 || height > 300) {
                    model.addAttribute("heightError", true);
                    model.addAttribute("error", "Height must be between 50 cm and 300 cm.");
                    hasError = true;
                }
            } catch (Exception e) {
                model.addAttribute("heightError", true);
                model.addAttribute("error", "Please enter a valid height.");
                hasError = true;
            }
        }

        double weight = 0;
        if (weightStr.isEmpty()) {
            model.addAttribute("weightError", true);
            hasError = true;
        } else {
            try {
                weight = Double.parseDouble(weightStr);

                if (weight < 20 || weight > 500) {
                    model.addAttribute("weightError", true);
                    model.addAttribute("error", "Weight must be between 20 kg and 500 kg.");
                    hasError = true;
                }
            } catch (Exception e) {
                model.addAttribute("weightError", true);
                model.addAttribute("error", "Please enter a valid weight.");
                hasError = true;
            }
        }

        int caloriesDailyGoal = 0;
        if (caloriesDailyGoalStr.isEmpty()) {
            model.addAttribute("caloriesDailyGoalError", true);
            hasError = true;
        } else {
            try {
                caloriesDailyGoal = Integer.parseInt(caloriesDailyGoalStr);

                if (caloriesDailyGoal < 500 || caloriesDailyGoal > 10000) {
                    model.addAttribute("caloriesDailyGoalError", true);
                    model.addAttribute("error", "Daily calorie goal must be between 500 and 10000.");
                    hasError = true;
                }
            } catch (Exception e) {
                model.addAttribute("caloriesDailyGoalError", true);
                model.addAttribute("error", "Please enter a valid daily calorie goal.");
                hasError = true;
            }
        }

        if (hasError) {
            model.addAttribute("user", user);
            return "users/profile";
        }

        user.setFirstname(firstname);
        user.setLastname(lastname);
        user.setSex(sex);
        user.setDateOfBirth(dateOfBirth);
        user.setHeight(height);
        user.setWeight(weight);
        user.setCaloriesDailyGoal(caloriesDailyGoal);

        userRepository.save(user);
        session.setAttribute("session_user", user);

        model.addAttribute("user", user);
        model.addAttribute("success", "Profile updated successfully.");

        return "users/profile";
    }

    // -- Helper Methods --

    /**
     * Validates that all required fields are filled.
     * 
     * @param data       Map containing user data.
     * @param model      Model to add attributes to.
     * @param fieldNames Array of field names to validate.
     * @return True if there are errors, false otherwise.
     */
    private boolean validateFields(Map<String, String> data, Model model, String... fieldNames) {
        boolean hasError = false;

        // Checks if all required fields are filled
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

    /**
     * Calculates the strength of a password.
     * 
     * @param password Password to calculate the strength of.
     * @return Strength of the password.
     */
    private int calculatePasswordStrength(String password) {
        int score = 0;

        // Checks if password is at least 1 character long
        if (password.length() > 0) {
            score++;
        }

        // Checks if password is at least 8 characters long
        if (password.length() >= 8) {
            score++;
        }

        // Checks if password contains at least one lowercase letter and one uppercase
        if (password.matches(".*[a-z].*") && password.matches(".*[A-Z].*")) {
            score++;
        }

        // Checks if password contains at least one number
        if (password.matches(".*[0-9].*")) {
            score++;
        }

        // Checks if password contains at least one special character
        if (password.matches(".*[!@#$%^&*].*")) {
            score++;
        }

        return score;
    }
}
