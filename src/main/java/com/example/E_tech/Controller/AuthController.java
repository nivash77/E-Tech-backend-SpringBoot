package com.example.E_tech.Controller;

import com.example.E_tech.Entity.User;
import com.example.E_tech.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String password = body.get("password");
        String role = body.get("role");

        User savedUser = userService.createUser(email, password, role);
        return ResponseEntity.ok(Map.of(
                "message", "User registered successfully",
                "userId", savedUser.getId()
        ));
    }

    @PostMapping("/send-code")
    public ResponseEntity<?> sendCode(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        userService.sendVerificationCode(email);
        return ResponseEntity.ok(Map.of("message", "Verification code sent to email"));
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verify(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String code = body.get("code");

        boolean verified = userService.verifyUser(email, code);
        return ResponseEntity.ok(Map.of(
                "message", "User verified successfully",
                "verified", verified
        ));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String password = body.get("password");

        Optional<User> optionalUser = userService.findByEmail(email);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (!user.isVerified()) {
                return ResponseEntity.status(403).body(Map.of(
                        "error", "Email not verified. Please verify before login."
                ));
            }
            if (userService.validatePassword(password, user.getPassword())) {
                return ResponseEntity.ok(Map.of(
                        "message", "Login successful",
                        "userId", user.getId(),
                        "role",user.getRole()
                ));
            }
        }
        return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
    }
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable String id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }
}
