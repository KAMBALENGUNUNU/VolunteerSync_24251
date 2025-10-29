package com.daniel4k.volunteersync.volunteersync.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Entity
@Table(name = "volunteers")
@Data

public class Volunteer {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long volunteerId;

    @NotBlank private String firstName;
    @NotBlank private String lastName;

    @NotBlank @Email @Column(unique = true)
    private String email;

    @NotBlank
    private String password; // store BCrypt-ed

    // Optional; validate only if present
    @Pattern(regexp = "^\\+?2507\\d{8}$", message = "Phone must be a valid Rwandan mobile like +2507XXXXXXXX")
    private String phone;

    @NotNull @Enumerated(EnumType.STRING)
    private Role role;

    @NotNull @ManyToOne @JoinColumn(name = "village_id", nullable = false)
    private Location village; // must be type VILLAGE
}
