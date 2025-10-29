package com.daniel4k.volunteersync.volunteersync.repository;

import com.daniel4k.volunteersync.volunteersync.model.Application;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
    boolean existsByVolunteer_VolunteerIdAndOpportunity_OpportunityId(Long volunteerId, Long opportunityId);

    Page<Application> findByVolunteer_VolunteerId(Long volunteerId, Pageable pageable);

    Page<Application> findByOpportunity_OpportunityId(Long opportunityId, Pageable pageable);
}
