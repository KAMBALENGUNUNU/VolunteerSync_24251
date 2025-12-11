package com.daniel4k.volunteersync.volunteersync.service;

import com.daniel4k.volunteersync.volunteersync.model.Application; // Ensure this import exists
import com.daniel4k.volunteersync.volunteersync.model.ApplicationStatus;
import com.daniel4k.volunteersync.volunteersync.repository.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Month;
import java.util.HashMap;
import java.util.LinkedHashMap; // Use LinkedHashMap to keep order
import java.util.List;
import java.util.Map;

@Service
public class DashboardService {

    private final VolunteerRepository volunteerRepository;
    private final NGORepository ngoRepository;
    private final OpportunityRepository opportunityRepository;
    private final ApplicationRepository applicationRepository;
    private final LocationRepository locationRepository;

    public DashboardService(VolunteerRepository volunteerRepository,
                            NGORepository ngoRepository,
                            OpportunityRepository opportunityRepository,
                            ApplicationRepository applicationRepository,
                            LocationRepository locationRepository) {
        this.volunteerRepository = volunteerRepository;
        this.ngoRepository = ngoRepository;
        this.opportunityRepository = opportunityRepository;
        this.applicationRepository = applicationRepository;
        this.locationRepository = locationRepository;
    }

    public Map<String, Object> getDashboardStatistics() {
        Map<String, Object> stats = new HashMap<>();

        // 1. Basic counts
        stats.put("totalVolunteers", volunteerRepository.count());
        stats.put("totalNGOs", ngoRepository.count());
        stats.put("totalOpportunities", opportunityRepository.count());
        stats.put("totalApplications", applicationRepository.count());
        stats.put("totalLocations", locationRepository.count());

        // 2. Application statistics by Status
        Map<String, Long> applicationStats = new HashMap<>();
        List<Application> allApps = applicationRepository.findAll();
        
        allApps.forEach(app -> {
            String status = app.getStatus().name();
            applicationStats.put(status, applicationStats.getOrDefault(status, 0L) + 1);
        });
        stats.put("applicationsByStatus", applicationStats);

        // 3. Upcoming opportunities
        long upcomingOpportunities = opportunityRepository.findAll().stream()
                .filter(opp -> opp.getEventDate().isAfter(LocalDate.now()))
                .count();
        stats.put("upcomingOpportunities", upcomingOpportunities);

        // 4. NEW: Monthly Application Trends (June to Dec)
        // We use LinkedHashMap to guarantee the order of months on the chart
        Map<String, Long> monthlyApps = new LinkedHashMap<>();
        String[] months = {"JUNE", "JULY", "AUGUST", "SEPTEMBER", "OCTOBER", "NOVEMBER", "DECEMBER"};
        
        // Initialize with 0
        for (String m : months) monthlyApps.put(m, 0L);

        // Fill with real data
        allApps.stream()
            .filter(app -> {
                Month m = app.getAppliedDate().getMonth();
                // Filter only for months >= JUNE
                return m.getValue() >= Month.JUNE.getValue();
            })
            .forEach(app -> {
                String mName = app.getAppliedDate().getMonth().name();
                if (monthlyApps.containsKey(mName)) {
                    monthlyApps.put(mName, monthlyApps.get(mName) + 1);
                }
            });

        stats.put("monthlyApplications", monthlyApps);

        return stats;
    }

    // ... (Keep the rest of the file: getVolunteerDashboard and getNGODashboard exactly as they were) ...
    public Map<String, Object> getVolunteerDashboard(Long volunteerId) {
        Map<String, Object> stats = new HashMap<>();
        long totalApplications = applicationRepository.findAll().stream()
                .filter(app -> app.getVolunteer().getVolunteerId().equals(volunteerId))
                .count();
        long pendingApplications = applicationRepository.findAll().stream()
                .filter(app -> app.getVolunteer().getVolunteerId().equals(volunteerId) 
                        && app.getStatus() == ApplicationStatus.PENDING)
                .count();
        long approvedApplications = applicationRepository.findAll().stream()
                .filter(app -> app.getVolunteer().getVolunteerId().equals(volunteerId) 
                        && app.getStatus() == ApplicationStatus.APPROVED)
                .count();
        stats.put("totalApplications", totalApplications);
        stats.put("pendingApplications", pendingApplications);
        stats.put("approvedApplications", approvedApplications);
        return stats;
    }

    public Map<String, Object> getNGODashboard(Long ngoId) {
        Map<String, Object> stats = new HashMap<>();
        long totalOpportunities = opportunityRepository.findAll().stream()
                .filter(opp -> opp.getNgo().getNgoId().equals(ngoId))
                .count();
        long totalApplications = applicationRepository.findAll().stream()
                .filter(app -> app.getOpportunity().getNgo().getNgoId().equals(ngoId))
                .count();
        long pendingApplications = applicationRepository.findAll().stream()
                .filter(app -> app.getOpportunity().getNgo().getNgoId().equals(ngoId) 
                        && app.getStatus() == ApplicationStatus.PENDING)
                .count();
        stats.put("totalOpportunities", totalOpportunities);
        stats.put("totalApplications", totalApplications);
        stats.put("pendingApplications", pendingApplications);
        return stats;
    }
}