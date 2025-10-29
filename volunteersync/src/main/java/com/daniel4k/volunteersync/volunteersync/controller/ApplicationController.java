package com.daniel4k.volunteersync.volunteersync.controller;

import com.daniel4k.volunteersync.volunteersync.model.Application;
import com.daniel4k.volunteersync.volunteersync.model.ApplicationStatus;
import com.daniel4k.volunteersync.volunteersync.service.ApplicationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/applications")
public class ApplicationController {
    private final ApplicationService service;

    public ApplicationController(ApplicationService service) {
        this.service = service;
    }

    @PostMapping("/apply")
    @PreAuthorize("hasAnyRole('VOLUNTEER','NGO_ADMIN')")
    public ResponseEntity<Application> apply(@RequestParam Long volunteerId, @RequestParam Long opportunityId) {
        return ResponseEntity.ok(service.apply(volunteerId, opportunityId));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('VOLUNTEER','NGO_ADMIN')")
    public ResponseEntity<Application> get(@PathVariable Long id) {
        return ResponseEntity.ok(service.get(id));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('NGO_ADMIN')")
    public ResponseEntity<Application> updateStatus(@PathVariable Long id, @RequestParam ApplicationStatus status) {
        return ResponseEntity.ok(service.updateStatus(id, status));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('NGO_ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/by-volunteer/{volunteerId}")
    @PreAuthorize("hasAnyRole('VOLUNTEER','NGO_ADMIN')")
    public ResponseEntity<Page<Application>> byVolunteer(@PathVariable Long volunteerId, Pageable pageable) {
        return ResponseEntity.ok(service.byVolunteer(volunteerId, pageable));
    }

    @GetMapping("/by-opportunity/{opportunityId}")
    @PreAuthorize("hasAnyRole('VOLUNTEER','NGO_ADMIN')")
    public ResponseEntity<Page<Application>> byOpportunity(@PathVariable Long opportunityId, Pageable pageable) {
        return ResponseEntity.ok(service.byOpportunity(opportunityId, pageable));
    }
}
