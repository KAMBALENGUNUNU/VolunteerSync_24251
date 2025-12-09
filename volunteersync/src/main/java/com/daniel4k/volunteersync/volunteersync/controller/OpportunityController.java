package com.daniel4k.volunteersync.volunteersync.controller;

import com.daniel4k.volunteersync.volunteersync.model.Opportunity;
import com.daniel4k.volunteersync.volunteersync.service.OpportunityService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController 
@RequestMapping("/api/opportunities")
public class OpportunityController {
    private final OpportunityService service;
    public OpportunityController(OpportunityService service){ this.service = service; }

    @PostMapping 
    @PreAuthorize("hasRole('NGO_ADMIN')")
    public ResponseEntity<Opportunity> create(@Valid @RequestBody Opportunity o){
        return ResponseEntity.ok(service.create(o));
    }

    @GetMapping("/{id}") 
    @PreAuthorize("hasAnyRole('VOLUNTEER','NGO_ADMIN')")
    public ResponseEntity<Opportunity> get(@PathVariable Long id){
        return ResponseEntity.ok(service.get(id));
    }

    @PutMapping("/{id}") 
    @PreAuthorize("hasRole('NGO_ADMIN')")
    public ResponseEntity<Opportunity> update(@PathVariable Long id, @Valid @RequestBody Opportunity o){
        return ResponseEntity.ok(service.update(id, o));
    }

    @DeleteMapping("/{id}") 
    @PreAuthorize("hasRole('NGO_ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        service.delete(id); return ResponseEntity.noContent().build();
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('VOLUNTEER','NGO_ADMIN')")
    public ResponseEntity<Page<Opportunity>> getAll(
            @RequestParam(required = false) String search,
            Pageable pageable) {
        if (search != null && !search.trim().isEmpty()) {
            return ResponseEntity.ok(service.searchOpportunities(search, pageable));
        }
        return ResponseEntity.ok(service.getAllOpportunities(pageable));
    }

    @GetMapping("/by-ngo/{ngoId}") 
    @PreAuthorize("hasAnyRole('VOLUNTEER','NGO_ADMIN')")
    public ResponseEntity<Page<Opportunity>> byNgo(@PathVariable Long ngoId, Pageable pageable){
        return ResponseEntity.ok(service.byNgo(ngoId, pageable));
    }

    @GetMapping("/by-village/{villageId}") 
    @PreAuthorize("hasAnyRole('VOLUNTEER','NGO_ADMIN')")
    public ResponseEntity<Page<Opportunity>> byVillage(@PathVariable Long villageId, Pageable pageable){
        return ResponseEntity.ok(service.byVillage(villageId, pageable));
    }
}