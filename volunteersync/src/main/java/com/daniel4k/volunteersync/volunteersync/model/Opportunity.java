package com.daniel4k.volunteersync.volunteersync.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "opportunities")
@Data
public class Opportunity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long opportunityId;

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @NotNull(message = "Event date is required")
    @Future(message = "Event date must be in the future")
    private LocalDate eventDate;

    @NotNull(message = "Max volunteers is required")
    @Min(value = 1, message = "Max volunteers must be at least 1")
    private Integer maxVolunteers;

    @NotNull(message = "NGO is required")
    @ManyToOne
    @JoinColumn(name = "ngo_id")
    private NGO ngo;

    @NotNull(message = "Village is required")
    @ManyToOne
    @JoinColumn(name = "village_id")
    private Location village;
}
