package com.daniel4k.volunteersync.volunteersync.service;

import com.daniel4k.volunteersync.volunteersync.model.ApplicationStatus;
import com.daniel4k.volunteersync.volunteersync.repository.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
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

        // Basic counts
        stats.put("totalVolunteers", volunteerRepository.count());
        stats.put("totalNGOs", ngoRepository.count());
        stats.put("totalOpportunities", opportunityRepository.count());
        stats.put("totalApplications", applicationRepository.count());
        stats.put("totalLocations", locationRepository.count());

        // Application statistics
        Map<String, Long> applicationStats = new HashMap<>();
        applicationRepository.findAll().forEach(app -> {
            String status = app.getStatus().name();
            applicationStats.put(status, applicationStats.getOrDefault(status, 0L) + 1);
        });
        stats.put("applicationsByStatus", applicationStats);

        // Upcoming opportunities count
        long upcomingOpportunities = opportunityRepository.findAll().stream()
                .filter(opp -> opp.getEventDate().isAfter(LocalDate.now()))
                .count();
        stats.put("upcomingOpportunities", upcomingOpportunities);

        return stats;
    }

    public Map<String, Object> getVolunteerDashboard(Long volunteerId) {
        Map<String, Object> stats = new HashMap<>();

        // Applications by this volunteer
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

        // Opportunities by this NGO
        long totalOpportunities = opportunityRepository.findAll().stream()
                .filter(opp -> opp.getNgo().getNgoId().equals(ngoId))
                .count();

        // Applications for this NGO's opportunities
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