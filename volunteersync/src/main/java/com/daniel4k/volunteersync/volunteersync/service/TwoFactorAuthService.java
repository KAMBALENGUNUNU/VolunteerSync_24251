package com.daniel4k.volunteersync.volunteersync.service;

import com.daniel4k.volunteersync.volunteersync.model.TwoFactorAuth;
import com.daniel4k.volunteersync.volunteersync.model.Volunteer;
import com.daniel4k.volunteersync.volunteersync.repository.TwoFactorAuthRepository;
import com.daniel4k.volunteersync.volunteersync.repository.VolunteerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class TwoFactorAuthService {

    private final TwoFactorAuthRepository twoFactorAuthRepository;
    private final VolunteerRepository volunteerRepository;
    private final EmailService emailService;

    public TwoFactorAuthService(TwoFactorAuthRepository twoFactorAuthRepository,
                                VolunteerRepository volunteerRepository,
                                EmailService emailService) {
        this.twoFactorAuthRepository = twoFactorAuthRepository;
        this.volunteerRepository = volunteerRepository;
        this.emailService = emailService;
    }

    @Transactional
    public void generate2FACode(String email) {
        Volunteer volunteer = volunteerRepository.findByEmail(email);
        if (volunteer == null) {
            throw new IllegalArgumentException("User not found");
        }

        // Delete old unverified codes
        twoFactorAuthRepository.findByVolunteerAndVerifiedFalse(volunteer)
                .ifPresent(twoFactorAuthRepository::delete);

        // Generate 6-digit code
        String code = String.format("%06d", new Random().nextInt(999999));

        // Create and save 2FA record
        TwoFactorAuth twoFactorAuth = new TwoFactorAuth();
        twoFactorAuth.setVolunteer(volunteer);
        twoFactorAuth.setCode(code);
        twoFactorAuth.setExpiryDate(LocalDateTime.now().plusMinutes(10)); // 10 minutes expiry
        twoFactorAuth.setVerified(false);

        twoFactorAuthRepository.save(twoFactorAuth);

        // Send email with code
        emailService.send2FACode(email, code);
    }

    @Transactional
    public boolean verify2FACode(String email, String code) {
        Volunteer volunteer = volunteerRepository.findByEmail(email);
        if (volunteer == null) {
            return false;
        }

        TwoFactorAuth twoFactorAuth = twoFactorAuthRepository.findByVolunteerAndVerifiedFalse(volunteer)
                .orElse(null);

        if (twoFactorAuth == null) {
            return false;
        }

        if (twoFactorAuth.isExpired()) {
            twoFactorAuthRepository.delete(twoFactorAuth);
            return false;
        }

        if (twoFactorAuth.getCode().equals(code)) {
            twoFactorAuth.setVerified(true);
            twoFactorAuthRepository.save(twoFactorAuth);
            return true;
        }

        return false;
    }

    @Transactional
    public void cleanupExpiredCodes() {
        twoFactorAuthRepository.deleteExpiredCodes(LocalDateTime.now());
    }
}