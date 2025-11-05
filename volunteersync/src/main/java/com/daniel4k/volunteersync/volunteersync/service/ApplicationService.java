package com.daniel4k.volunteersync.volunteersync.service;

import com.daniel4k.volunteersync.volunteersync.model.*;
import com.daniel4k.volunteersync.volunteersync.repository.ApplicationRepository;
import com.daniel4k.volunteersync.volunteersync.repository.OpportunityRepository;
import com.daniel4k.volunteersync.volunteersync.repository.VolunteerRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Service
public class ApplicationService {
    private final ApplicationRepository applicationRepository;
    private final VolunteerRepository volunteerRepository;
    private final OpportunityRepository opportunityRepository;

    public ApplicationService(ApplicationRepository applicationRepository, VolunteerRepository volunteerRepository,OpportunityRepository opportunityRepository) {
        this.applicationRepository = applicationRepository;
        this.volunteerRepository = volunteerRepository;
        this.opportunityRepository = opportunityRepository;
    }

    @Transactional
    public Application apply(Long volunteerId, Long opportunityId) {
              Volunteer v = volunteerRepository.findById(volunteerId)
                .orElseThrow(() -> new IllegalArgumentException("Volunteer not found"));
              Opportunity o = opportunityRepository.findById(opportunityId)
                .orElseThrow(() -> new IllegalArgumentException("Opportunity not found"));

        if (applicationRepository.existsByVolunteer_VolunteerIdAndOpportunity_OpportunityId(volunteerId, opportunityId))
            throw new IllegalArgumentException("Duplicate application");

        Application app = new Application();
        app.setVolunteer(v);
        app.setOpportunity(o);
        app.setStatus(ApplicationStatus.PENDING);
        app.setAppliedDate(LocalDateTime.now());
        return applicationRepository.save(app);
    }

    public Application get(Long id) {
        return applicationRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Application not found"));
    }

    @Transactional
    public Application updateStatus(Long id, ApplicationStatus status) {
        Application app = get(id);
        app.setStatus(status);
        return applicationRepository.save(app);
    }

    @Transactional
    public void delete(Long id) {
        if (!applicationRepository.existsById(id))
            throw new EntityNotFoundException("Application not found");
        applicationRepository.deleteById(id);
    }

    public Page<Application> byVolunteer(Long volunteerId, Pageable pageable) {
        return applicationRepository.findByVolunteer_VolunteerId(volunteerId, pageable);
    }

    public Page<Application> byOpportunity(Long opportunityId, Pageable pageable) {
        return applicationRepository.findByOpportunity_OpportunityId(opportunityId, pageable);
    }
}
