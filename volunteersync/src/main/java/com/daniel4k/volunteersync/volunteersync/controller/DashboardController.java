package com.daniel4k.volunteersync.volunteersync.controller;

import com.daniel4k.volunteersync.volunteersync.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/statistics")
    @PreAuthorize("hasRole('NGO_ADMIN')")
    public ResponseEntity<Map<String, Object>> getStatistics() {
        return ResponseEntity.ok(dashboardService.getDashboardStatistics());
    }

    @GetMapping("/volunteer/{volunteerId}")
    @PreAuthorize("hasAnyRole('VOLUNTEER','NGO_ADMIN')")
    public ResponseEntity<Map<String, Object>> getVolunteerDashboard(@PathVariable Long volunteerId) {
        return ResponseEntity.ok(dashboardService.getVolunteerDashboard(volunteerId));
    }

    @GetMapping("/ngo/{ngoId}")
    @PreAuthorize("hasRole('NGO_ADMIN')")
    public ResponseEntity<Map<String, Object>> getNGODashboard(@PathVariable Long ngoId) {
        return ResponseEntity.ok(dashboardService.getNGODashboard(ngoId));
    }
}