package com.smart.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.dao.UserRepository;
import com.smart.entities.User;
import com.smart.helper.Message;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class HomeController {
	@Autowired
private BCryptPasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

  
    public HomeController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("title", "Home - Smart Contact Manager");
        return "home";
    }

    @GetMapping("/about")
    public String about(Model model) {
        model.addAttribute("title", "About - Smart Contact Manager");
        return "about";
    }

    @GetMapping("/signup")
    public String signup(Model model) {
        model.addAttribute("title", "Register - Smart Contact Manager");
        model.addAttribute("user", new User());
        return "signup";
    }

    @PostMapping("/doregister")
    public String registerUser(@Valid @ModelAttribute("user") User user,
                               @RequestParam(value = "agreement", defaultValue = "false") boolean agreement,
                               BindingResult result,
                               Model model,
                               HttpSession session) {
        try {
            if (!agreement) {
                throw new IllegalArgumentException("You have not agreed to the terms and conditions.");
            }

            if (result.hasErrors()) {
                System.out.println("Errors: " + result.getAllErrors());
                model.addAttribute("user", user);
                return "signup";
            }

            user.setRole("ROLE_USER");
            user.setEnabled(true);
            user.setImageUrl("bg.jpg");
            user.setPassword(passwordEncoder.encode(user.getPassword()));
          User savedUser = userRepository.save(user);
            model.addAttribute("user", new User());
            session.setAttribute("message", new Message("Successfully registered", "alert-success"));
            return "signup";
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            model.addAttribute("user", user);
            session.setAttribute("message", new Message("Something went wrong: " + e.getMessage(), "alert-danger"));
            return "signup";
        }
    }
    @GetMapping("/signin")
    public String customlogin(Model model) {
        model.addAttribute("title", "login-page");
        
        return "login";
    }
    @GetMapping("/loginfail")
    public String loginffail(Model model) {
        model.addAttribute("title", "login-page-fail");
        
        return "loginfail";
    }

}
