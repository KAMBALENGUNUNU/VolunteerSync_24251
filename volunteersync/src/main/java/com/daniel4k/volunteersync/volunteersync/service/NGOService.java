package com.daniel4k.volunteersync.volunteersync.service;

import com.daniel4k.volunteersync.volunteersync.model.NGO;
import com.daniel4k.volunteersync.volunteersync.model.Role;
import com.daniel4k.volunteersync.volunteersync.model.Volunteer;
import com.daniel4k.volunteersync.volunteersync.repository.NGORepository;
import com.daniel4k.volunteersync.volunteersync.repository.VolunteerRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NGOService {
    private final NGORepository ngoRepository;
    private final VolunteerRepository volunteerRepository;

    public NGOService(NGORepository ngoRepository, VolunteerRepository volunteerRepository) {
        this.ngoRepository = ngoRepository;
        this.volunteerRepository = volunteerRepository;
    }

    @Transactional
    public NGO create(@Valid NGO ngo) {
        if (ngoRepository.existsByContactEmail(ngo.getContactEmail()))
            throw new IllegalArgumentException("NGO contact email already exists");

        Volunteer admin = volunteerRepository.findById(ngo.getAdmin().getVolunteerId()).orElseThrow(() -> new IllegalArgumentException("Admin volunteer not found"));

        if (admin.getRole() != Role.NGO_ADMIN)
            throw new IllegalArgumentException("Admin must have role NGO_ADMIN");

        ngo.setAdmin(admin);
        return ngoRepository.save(ngo);
    }

    public NGO get(Long id) {
        return ngoRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("NGO not found"));
    }

    @Transactional
    public NGO update(Long id, @Valid NGO incoming) {
        NGO existing = get(id);
        if (!existing.getContactEmail().equalsIgnoreCase(incoming.getContactEmail())
                && ngoRepository.existsByContactEmail(incoming.getContactEmail())) {
            throw new IllegalArgumentException("NGO contact email already exists");
        }
        existing.setName(incoming.getName());
        existing.setDescription(incoming.getDescription());
        existing.setContactEmail(incoming.getContactEmail());

        Volunteer admin = volunteerRepository.findById(incoming.getAdmin().getVolunteerId())
                .orElseThrow(() -> new IllegalArgumentException("Admin volunteer not found"));
        if (admin.getRole() != Role.NGO_ADMIN)
            throw new IllegalArgumentException("Admin must have role NGO_ADMIN");
        existing.setAdmin(admin);
        return ngoRepository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        if (!ngoRepository.existsById(id))
            throw new EntityNotFoundException("NGO not found");
        ngoRepository.deleteById(id);
    }
}
