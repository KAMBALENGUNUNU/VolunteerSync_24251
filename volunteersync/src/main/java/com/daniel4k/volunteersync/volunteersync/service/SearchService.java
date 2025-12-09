package com.daniel4k.volunteersync.volunteersync.service;

import com.daniel4k.volunteersync.volunteersync.repository.*;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SearchService {

    private final VolunteerRepository volunteerRepository;
    private final NGORepository ngoRepository;
    private final OpportunityRepository opportunityRepository;
    private final LocationRepository locationRepository;

    public SearchService(VolunteerRepository volunteerRepository,
                        NGORepository ngoRepository,
                        OpportunityRepository opportunityRepository,
                        LocationRepository locationRepository) {
        this.volunteerRepository = volunteerRepository;
        this.ngoRepository = ngoRepository;
        this.opportunityRepository = opportunityRepository;
        this.locationRepository = locationRepository;
    }

    public Map<String, Object> globalSearch(String query) {
        Map<String, Object> results = new HashMap<>();
        String lowerQuery = query.toLowerCase();

        // Search volunteers
        var volunteers = volunteerRepository.findAll().stream()
                .filter(v -> v.getFirstName().toLowerCase().contains(lowerQuery) ||
                           v.getLastName().toLowerCase().contains(lowerQuery) ||
                           v.getEmail().toLowerCase().contains(lowerQuery))
                .collect(Collectors.toList());
        results.put("volunteers", volunteers);

        // Search NGOs
        var ngos = ngoRepository.findAll().stream()
                .filter(n -> n.getName().toLowerCase().contains(lowerQuery) ||
                           (n.getDescription() != null && n.getDescription().toLowerCase().contains(lowerQuery)) ||
                           n.getContactEmail().toLowerCase().contains(lowerQuery))
                .collect(Collectors.toList());
        results.put("ngos", ngos);

        // Search opportunities
        var opportunities = opportunityRepository.findAll().stream()
                .filter(o -> o.getTitle().toLowerCase().contains(lowerQuery) ||
                           (o.getDescription() != null && o.getDescription().toLowerCase().contains(lowerQuery)))
                .collect(Collectors.toList());
        results.put("opportunities", opportunities);

        // Search locations
        var locations = locationRepository.findAll().stream()
                .filter(l -> l.getName().toLowerCase().contains(lowerQuery) ||
                           l.getCode().toLowerCase().contains(lowerQuery))
                .collect(Collectors.toList());
        results.put("locations", locations);

        return results;
    }
}