package com.daniel4k.volunteersync.volunteersync.controller;

import com.daniel4k.volunteersync.volunteersync.model.Volunteer;
import com.daniel4k.volunteersync.volunteersync.service.VolunteerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/volunteers")
public class VolunteerController {
    @Autowired
    private VolunteerService volunteerService;

    @PostMapping
    @PreAuthorize("hasRole('NGO_ADMIN')")
    public ResponseEntity<Volunteer> createVolunteer(@Valid @RequestBody Volunteer volunteer) {
        return ResponseEntity.ok(volunteerService.createVolunteer(volunteer));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('VOLUNTEER', 'NGO_ADMIN')")
    public ResponseEntity<Volunteer> getVolunteer(@PathVariable Long id) {
        return ResponseEntity.ok(volunteerService.getVolunteer(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('NGO_ADMIN')")
    public ResponseEntity<Volunteer> updateVolunteer(@PathVariable Long id, @Valid @RequestBody Volunteer volunteer) {
        return ResponseEntity.ok(volunteerService.updateVolunteer(id, volunteer));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('NGO_ADMIN')")
    public ResponseEntity<Void> deleteVolunteer(@PathVariable Long id) {
        volunteerService.deleteVolunteer(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('NGO_ADMIN')")
    public ResponseEntity<Page<Volunteer>> getAllVolunteers(
            @RequestParam(required = false) String search,
            Pageable pageable) {
        if (search != null && !search.trim().isEmpty()) {
            return ResponseEntity.ok(volunteerService.searchVolunteers(search, pageable));
        }
        return ResponseEntity.ok(volunteerService.getAllVolunteers(pageable));
    }

    @GetMapping("/by-village/{villageId}")
    @PreAuthorize("hasAnyRole('VOLUNTEER', 'NGO_ADMIN')")
    public ResponseEntity<Page<Volunteer>> getVolunteersByVillage(@PathVariable Long villageId, Pageable pageable) {
        return ResponseEntity.ok(volunteerService.getVolunteersByVillage(villageId, pageable));
    }

    @GetMapping("/by-province")
    @PreAuthorize("hasAnyRole('VOLUNTEER', 'NGO_ADMIN')")
    public ResponseEntity<List<Volunteer>> getVolunteersByProvince(
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String name) {
        if (code != null) {
            return ResponseEntity.ok(volunteerService.getVolunteersByProvinceCode(code));
        } else if (name != null) {
            return ResponseEntity.ok(volunteerService.getVolunteersByProvinceName(name));
        }
        return ResponseEntity.badRequest().build();
    }
}