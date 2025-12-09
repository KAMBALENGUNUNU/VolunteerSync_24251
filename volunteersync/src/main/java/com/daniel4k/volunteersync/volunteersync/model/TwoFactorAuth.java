package com.daniel4k.volunteersync.volunteersync.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "two_factor_auth")
@Data
public class TwoFactorAuth {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @OneToOne
    @JoinColumn(name = "volunteer_id", nullable = false, unique = true)
    private Volunteer volunteer;

    @NotNull
    private String code;

    @NotNull
    private LocalDateTime expiryDate;

    private boolean verified = false;

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expiryDate);
    }
}