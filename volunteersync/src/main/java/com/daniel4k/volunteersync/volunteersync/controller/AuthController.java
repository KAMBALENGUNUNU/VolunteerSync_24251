package com.daniel4k.volunteersync.volunteersync.controller;

import com.daniel4k.volunteersync.volunteersync.model.Role;
import com.daniel4k.volunteersync.volunteersync.model.Volunteer;
import com.daniel4k.volunteersync.volunteersync.repository.VolunteerRepository;
import com.daniel4k.volunteersync.volunteersync.service.PasswordResetService;
import com.daniel4k.volunteersync.volunteersync.service.TwoFactorAuthService;
import com.daniel4k.volunteersync.volunteersync.service.UserDetailsServiceImpl;
import com.daniel4k.volunteersync.volunteersync.service.VolunteerService;
import com.daniel4k.volunteersync.volunteersync.util.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsServiceImpl userDetailsService;
    private final JwtUtil jwtUtil;
    private final TwoFactorAuthService twoFactorAuthService;
    private final PasswordResetService passwordResetService;
    private final VolunteerService volunteerService;
    private final VolunteerRepository volunteerRepository;

    public AuthController(AuthenticationManager authenticationManager,
                          UserDetailsServiceImpl userDetailsService,
                          JwtUtil jwtUtil,
                          TwoFactorAuthService twoFactorAuthService,
                          PasswordResetService passwordResetService,
                          VolunteerService volunteerService,     
                          VolunteerRepository volunteerRepository 
                          ) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
        this.twoFactorAuthService = twoFactorAuthService;
        this.passwordResetService = passwordResetService;
        this.volunteerService = volunteerService;
        this.volunteerRepository = volunteerRepository;
    }

    // --- FIX B: NEW ENDPOINTS START ---

    // 1. PUBLIC REGISTRATION ENDPOINT
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody Volunteer volunteer) {
        try {
            // Force role to VOLUNTEER for public registration for safety
            volunteer.setRole(Role.VOLUNTEER);
            
            // Password encoding is handled inside volunteerService.createVolunteer
            return ResponseEntity.ok(volunteerService.createVolunteer(volunteer));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 2. GET CURRENT USER PROFILE
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(401).body("Not Authenticated");
        }
        String email = authentication.getName();
        Volunteer volunteer = volunteerRepository.findByEmail(email); 
            
        if (volunteer != null) {
            // Avoid sending password back!
            volunteer.setPassword(null); 
            return ResponseEntity.ok(volunteer);
        }
        return ResponseEntity.notFound().build();
    }

    // 3. LOGOUT (Stateless)
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid credentials");
        }

        // Generate and send 2FA code
        try {
            twoFactorAuthService.generate2FACode(loginRequest.getEmail());
            Map<String, String> response = new HashMap<>();
            response.put("message", "2FA code sent to your email");
            response.put("email", loginRequest.getEmail());
            response.put("requiresTwoFactor", "true");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error sending 2FA code: " + e.getMessage());
        }
    }

    @PostMapping("/verify-2fa")
    public ResponseEntity<?> verify2FA(@RequestBody TwoFactorRequest request) {
        boolean isValid = twoFactorAuthService.verify2FACode(request.getEmail(), request.getCode());
        
        if (!isValid) {
            return ResponseEntity.badRequest().body("Invalid or expired 2FA code");
        }

        // Generate JWT token after successful 2FA
        final UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        final String jwt = jwtUtil.generateToken(userDetails);

        Map<String, String> response = new HashMap<>();
        response.put("token", jwt);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        try {
            passwordResetService.createPasswordResetToken(request.getEmail());
            return ResponseEntity.ok(Map.of("message", "Password reset email sent"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        try {
            passwordResetService.resetPassword(request.getToken(), request.getNewPassword());
            return ResponseEntity.ok(Map.of("message", "Password reset successful"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    // Request DTOs
    public static class LoginRequest {
        private String email;
        private String password;

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class TwoFactorRequest {
        private String email;
        private String code;

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getCode() { return code; }
        public void setCode(String code) { this.code = code; }
    }

    public static class ForgotPasswordRequest {
        private String email;

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }

    public static class ResetPasswordRequest {
        private String token;
        private String newPassword;

        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }
        public String getNewPassword() { return newPassword; }
        public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
    }
}