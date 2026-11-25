package csd230.lab1.auth;

import csd230.lab1.services.CustomUserDetailsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/rest/auth")
public class AuthController {

    private final DaoAuthenticationProvider authenticationProvider;
    private final CustomUserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;

    public AuthController(
            DaoAuthenticationProvider authenticationProvider,
            CustomUserDetailsService userDetailsService,
            JwtUtil jwtUtil) {

        this.authenticationProvider = authenticationProvider;
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody Map<String, String> request) {

        String username = request.get("username");
        String password = request.get("password");

        if (username == null || password == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message",
                            "Username and password are required"));
        }

        try {

            authenticationProvider.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            username, password));

            UserDetails userDetails =
                    userDetailsService.loadUserByUsername(username);

            String token = jwtUtil.generateToken(userDetails);

            return ResponseEntity.ok(Map.of("token", token));

        } catch (AuthenticationException e) {

            return ResponseEntity.badRequest()
                    .body(Map.of("message",
                            "Invalid username or password"));
        }
    }
}