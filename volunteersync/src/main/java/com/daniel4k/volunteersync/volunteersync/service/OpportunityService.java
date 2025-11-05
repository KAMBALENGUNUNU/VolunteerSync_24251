package com.daniel4k.volunteersync.volunteersync.service;

import com.daniel4k.volunteersync.volunteersync.model.Location;
import com.daniel4k.volunteersync.volunteersync.model.LocationType;
import com.daniel4k.volunteersync.volunteersync.model.NGO;
import com.daniel4k.volunteersync.volunteersync.model.Opportunity;
import com.daniel4k.volunteersync.volunteersync.repository.LocationRepository;
import com.daniel4k.volunteersync.volunteersync.repository.NGORepository;
import com.daniel4k.volunteersync.volunteersync.repository.OpportunityRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class OpportunityService {
    private final OpportunityRepository opportunityRepository;
    private final NGORepository ngoRepository;
    private final LocationRepository locationRepository;

    public OpportunityService(OpportunityRepository opportunityRepository,NGORepository ngoRepository,LocationRepository locationRepository) {
        this.opportunityRepository = opportunityRepository;
        this.ngoRepository = ngoRepository;
        this.locationRepository = locationRepository;
    }

    @Transactional
    public Opportunity create(@Valid Opportunity o) {
        NGO ngo = ngoRepository.findById(o.getNgo().getNgoId()).orElseThrow(() -> new IllegalArgumentException("NGO not found"));
        Location village = locationRepository.findById(o.getVillage().getLocationId()) .orElseThrow(() -> new IllegalArgumentException("Village not found"));
        if (village.getType() != LocationType.VILLAGE)
            throw new IllegalArgumentException("Opportunity.village must be of type VILLAGE");
        if (o.getEventDate().isBefore(LocalDate.now()))
            throw new IllegalArgumentException("Event date must be today or in the future");

        o.setNgo(ngo);
        o.setVillage(village);
        return opportunityRepository.save(o);
    }

    public Opportunity get(Long id) {
        return opportunityRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Opportunity not found"));
    }

    @Transactional
    public Opportunity update(Long id, @Valid Opportunity incoming) {
        Opportunity existing = get(id);
        existing.setTitle(incoming.getTitle());
        existing.setDescription(incoming.getDescription());
        existing.setEventDate(incoming.getEventDate());
        existing.setMaxVolunteers(incoming.getMaxVolunteers());

        // reattach relations
        existing.setNgo(ngoRepository.findById(incoming.getNgo().getNgoId())
                .orElseThrow(() -> new IllegalArgumentException("NGO not found")));
        Location village = locationRepository.findById(incoming.getVillage().getLocationId())
                .orElseThrow(() -> new IllegalArgumentException("Village not found"));
        if (village.getType() != LocationType.VILLAGE)
            throw new IllegalArgumentException("Opportunity.village must be VILLAGE");
        existing.setVillage(village);
        return opportunityRepository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        if (!opportunityRepository.existsById(id))
            throw new EntityNotFoundException("Opportunity not found");
        opportunityRepository.deleteById(id);
    }

    public Page<Opportunity> byNgo(Long ngoId, Pageable pageable) {
        // return opportunityRepository.findByNgo_NgoId(ngoId, pageable);
        return opportunityRepository.findByNgoId(ngoId, pageable);
    }

    public Page<Opportunity> byVillage(Long villageId, Pageable pageable) {
        // return opportunityRepository.findByVillage_LocationId(villageId, pageable);
        return opportunityRepository.findByVillageId(villageId, pageable);
    }
}
