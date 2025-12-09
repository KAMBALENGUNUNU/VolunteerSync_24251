package com.daniel4k.volunteersync.volunteersync.service;

import com.daniel4k.volunteersync.volunteersync.model.PasswordResetToken;
import com.daniel4k.volunteersync.volunteersync.model.Volunteer;
import com.daniel4k.volunteersync.volunteersync.repository.PasswordResetTokenRepository;
import com.daniel4k.volunteersync.volunteersync.repository.VolunteerRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PasswordResetService {

    private final PasswordResetTokenRepository tokenRepository;
    private final VolunteerRepository volunteerRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    public PasswordResetService(PasswordResetTokenRepository tokenRepository,
                                VolunteerRepository volunteerRepository,
                                EmailService emailService,
                                PasswordEncoder passwordEncoder) {
        this.tokenRepository = tokenRepository;
        this.volunteerRepository = volunteerRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void createPasswordResetToken(String email) {
        Volunteer volunteer = volunteerRepository.findByEmail(email);
        if (volunteer == null) {
            throw new IllegalArgumentException("No user found with email: " + email);
        }

        // Delete old token if exists
        tokenRepository.findByVolunteer(volunteer).ifPresent(tokenRepository::delete);

        // Create new token
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(UUID.randomUUID().toString());
        resetToken.setVolunteer(volunteer);
        resetToken.setExpiryDate(LocalDateTime.now().plusHours(1)); // 1 hour expiry
        resetToken.setUsed(false);

        tokenRepository.save(resetToken);

        // Send email
        emailService.sendPasswordResetEmail(email, resetToken.getToken());
    }

    @Transactional
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid password reset token"));

        if (resetToken.isUsed()) {
            throw new IllegalArgumentException("Token has already been used");
        }

        if (resetToken.isExpired()) {
            throw new IllegalArgumentException("Token has expired");
        }

        // Update password
        Volunteer volunteer = resetToken.getVolunteer();
        volunteer.setPassword(passwordEncoder.encode(newPassword));
        volunteerRepository.save(volunteer);

        // Mark token as used
        resetToken.setUsed(true);
        tokenRepository.save(resetToken);
    }

    @Transactional
    public void cleanupExpiredTokens() {
        tokenRepository.deleteExpiredTokens(LocalDateTime.now());
    }
}