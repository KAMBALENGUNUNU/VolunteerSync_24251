package com.daniel4k.volunteersync.volunteersync.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "applications",uniqueConstraints = @UniqueConstraint(columnNames = {"volunteer_id","opportunity_id"}))

@Data
public class Application {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long applicationId;

    @NotNull 
    @ManyToOne 
    @JoinColumn(name = "volunteer_id", nullable = false)
    private Volunteer volunteer;

    @NotNull 
    @ManyToOne 
    @JoinColumn(name = "opportunity_id", nullable = false)
    private Opportunity opportunity;

    @NotNull 
    @Enumerated(EnumType.STRING)
    private ApplicationStatus status;

    @NotNull
    private LocalDateTime appliedDate;
}