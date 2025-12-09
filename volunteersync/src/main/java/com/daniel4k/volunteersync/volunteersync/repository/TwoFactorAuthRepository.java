package com.daniel4k.volunteersync.volunteersync.repository;

import com.daniel4k.volunteersync.volunteersync.model.TwoFactorAuth;
import com.daniel4k.volunteersync.volunteersync.model.Volunteer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface TwoFactorAuthRepository extends JpaRepository<TwoFactorAuth, Long> {
    Optional<TwoFactorAuth> findByVolunteerAndVerifiedFalse(Volunteer volunteer);
    
    @Modifying
    @Query("DELETE FROM TwoFactorAuth t WHERE t.expiryDate < :now")
    void deleteExpiredCodes(@Param("now") LocalDateTime now);
}