package ca.humber.huynh.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ca.humber.huynh.database.DatabaseAccess;

@Controller
public class RegisterController {

    @Autowired
    private DatabaseAccess databaseAccess;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @PostMapping("/register")
    public String processRegistration(@RequestParam String email, 
                                      @RequestParam String password, 
                                      Model model) {
        String encryptedPassword = passwordEncoder.encode(password);
        boolean success = databaseAccess.registerUser(email, encryptedPassword);
        
        if (success) {
            return "redirect:/login?registered";
        } else {
            model.addAttribute("error", "Email already exists.");
            return "register";
        }
    }
}