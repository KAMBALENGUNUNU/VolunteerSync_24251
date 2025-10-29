package com.daniel4k.volunteersync.volunteersync.controller;

import com.daniel4k.volunteersync.volunteersync.model.NGO;
import com.daniel4k.volunteersync.volunteersync.service.NGOService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ngos")
public class NGOController {
    private final NGOService ngoService;
    public NGOController(NGOService ngoService){ this.ngoService = ngoService; }

    @PostMapping 
    @PreAuthorize("hasRole('NGO_ADMIN')")
    public ResponseEntity<NGO> create(@Valid @RequestBody NGO ngo) {
        return ResponseEntity.ok(ngoService.create(ngo));
    }

    @GetMapping("/{id}") 
    @PreAuthorize("hasAnyRole('VOLUNTEER','NGO_ADMIN')")
    public ResponseEntity<NGO> get(@PathVariable Long id){
        return ResponseEntity.ok(ngoService.get(id));
    }

    @PutMapping("/{id}") 
    @PreAuthorize("hasRole('NGO_ADMIN')")
    public ResponseEntity<NGO> update(@PathVariable Long id, @Valid @RequestBody NGO ngo){
        return ResponseEntity.ok(ngoService.update(id, ngo));
    }

    @DeleteMapping("/{id}") 
    @PreAuthorize("hasRole('NGO_ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        ngoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
