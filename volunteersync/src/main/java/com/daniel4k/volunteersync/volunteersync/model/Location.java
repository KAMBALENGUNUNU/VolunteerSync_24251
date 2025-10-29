package com.daniel4k.volunteersync.volunteersync.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity @Table(name = "locations")
@Data

public class Location {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long locationId;

    @NotBlank private String name;

    @NotBlank 
    @Column(unique = true)
    private String code;

    @NotNull 
    @Enumerated(EnumType.STRING)
    private LocationType type;

    @ManyToOne 
    @JoinColumn(name = "parent_id")
    private Location parent; // null only for PROVINCE
}
