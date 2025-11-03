package com.daniel4k.volunteersync.volunteersync.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Table(name = "ngos")
@Data

public class NGO {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ngoId;

    @NotBlank private String name;

    private String description;

    @NotBlank 
    @Email 
    @Column(unique = true)
    private String contactEmail;

    @NotNull
    @OneToOne 
    @JoinColumn(name = "admin_id", unique = true, nullable = false)
    private Volunteer admin; 
}
