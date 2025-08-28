package com.example.E_tech.Service;

import com.example.E_tech.Entity.User;
import com.example.E_tech.Repository.UserRepository;
import com.example.E_tech.Util.PasswordUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final EmailService emailService;

    public UserService(UserRepository userRepository, EmailService emailService) {
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    public User createUser(String email, String password, String role,String name) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already registered");
        }
        User user = new User();
        user.setEmail(email);
        user.setName(name);
        user.setPassword(PasswordUtil.hashPassword(password));
        user.setRole(role);
        user.setCreatedAt(LocalDateTime.now());
        user.setVerified(false);
        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User updateUser(String id, String email, String role) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        user.setEmail(email);
        user.setRole(role);
        return userRepository.save(user);
    }

    public void deleteUser(String id) {
        if (!userRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        userRepository.deleteById(id);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public boolean validatePassword(String rawPassword, String hashedPassword) {
        return PasswordUtil.checkPassword(rawPassword, hashedPassword);
    }

    public boolean existsById(String id) {
        return userRepository.existsById(id);
    }

    public void sendVerificationCode(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        String code = generateVerificationCode();
        user.setVerificationCode(code);
        user.setVerificationCodeExpiry(LocalDateTime.now().plusMinutes(10));
        userRepository.save(user);

        emailService.sendVerificationEmail(email, code);
    }

    public boolean verifyUser(String email, String code) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (user.getVerificationCode() == null || user.getVerificationCodeExpiry() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No verification code found");
        }

        if (LocalDateTime.now().isAfter(user.getVerificationCodeExpiry())) {
            user.setVerificationCode(null);
            user.setVerificationCodeExpiry(null);
            userRepository.save(user);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Verification code expired");
        }

        if (!user.getVerificationCode().equals(code)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid verification code");
        }

        user.setVerified(true);
        user.setVerificationCode(null);
        user.setVerificationCodeExpiry(null);
        userRepository.save(user);

        return true;
    }

    private String generateVerificationCode() {
        return String.valueOf(100000 + new Random().nextInt(900000)); // 6-digit code
    }
    public boolean isTeacher(String email) {
        return userRepository.findByEmail(email)
                .map(user -> "teacher".equalsIgnoreCase(user.getRole()))
                .orElse(false);
    }

    public User findByEmailWithoutOptional(String email) {
        Optional<User> user=userRepository.findByEmail(email);
        return user.orElse(null);
    }

    public String getRoleByEmail(String email) {
        User user=userRepository.findByEmail(email).orElseThrow(()->new RuntimeException("User not found"));
        return user.getRole();
    }

    public User getUserById(String userId) {

        return userRepository.findById(userId).orElse(null);
    }
}
