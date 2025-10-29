package com.daniel4k.volunteersync.volunteersync.controller;

import com.daniel4k.volunteersync.volunteersync.model.Location;
import com.daniel4k.volunteersync.volunteersync.service.LocationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController 
@RequestMapping("/api/locations")
public class LocationController {
    private final LocationService service;
    public LocationController(LocationService service){ this.service = service; }

    @PostMapping 
    @PreAuthorize("hasRole('NGO_ADMIN')")
    public ResponseEntity<Location> create(@Valid @RequestBody Location l){ return ResponseEntity.ok(service.createLocation(l)); }

    @GetMapping("/{id}") 
    @PreAuthorize("hasAnyRole('VOLUNTEER','NGO_ADMIN')")
    public ResponseEntity<Location> get(@PathVariable Long id){ return ResponseEntity.ok(service.getLocation(id)); }

    @PutMapping("/{id}") 
    @PreAuthorize("hasRole('NGO_ADMIN')")
    public ResponseEntity<Location> update(@PathVariable Long id, @Valid @RequestBody Location l){
        return ResponseEntity.ok(service.updateLocation(id, l));
    }

    @DeleteMapping("/{id}") 
    @PreAuthorize("hasRole('NGO_ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        service.deleteLocation(id); return ResponseEntity.noContent().build();
    }
}
