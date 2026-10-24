
package csd230.lab1.controllers;

import csd230.lab1.entities.UserEntity;
import csd230.lab1.repositories.UserEntityRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class RegisterController {

    private final UserEntityRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public RegisterController(
            UserEntityRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/register")
    public String showRegistrationForm() {
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(
            @RequestParam String username,
            @RequestParam String password
    ) {

        if (userRepository.findByUsername(username) != null) {
            return "redirect:/register?error";
        }

        UserEntity newUser = new UserEntity(
                username,
                passwordEncoder.encode(password),
                "USER"
        );

        userRepository.save(newUser);

        return "redirect:/login?registered";
    }
}
