package com.daniel4k.volunteersync.volunteersync.service;

import com.daniel4k.volunteersync.volunteersync.model.Volunteer;
import com.daniel4k.volunteersync.volunteersync.repository.VolunteerRepository;
import com.daniel4k.volunteersync.volunteersync.repository.LocationRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
public class VolunteerService {
    @Autowired
    private VolunteerRepository volunteerRepository;
    @Autowired
    private LocationRepository locationRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @Transactional
    public Volunteer createVolunteer(@Valid Volunteer volunteer) {
        if (volunteerRepository.existsByEmail(volunteer.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }
        if (!locationRepository.existsById(volunteer.getVillage().getLocationId())) {
            throw new IllegalArgumentException("Invalid village ID");
        }
        volunteer.setPassword(passwordEncoder.encode(volunteer.getPassword()));
        return volunteerRepository.save(volunteer);
    }

    public Volunteer getVolunteer(Long id) {
        return volunteerRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Volunteer not found"));
    }

    @Transactional
    public Volunteer updateVolunteer(Long id, @Valid Volunteer volunteer) {
        Volunteer existing = getVolunteer(id);
        if (!existing.getEmail().equals(volunteer.getEmail())&& volunteerRepository.existsByEmail(volunteer.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }
        existing.setFirstName(volunteer.getFirstName());
        existing.setLastName(volunteer.getLastName());
        existing.setEmail(volunteer.getEmail());
        if (volunteer.getPassword() != null && !volunteer.getPassword().isEmpty()) {
            existing.setPassword(passwordEncoder.encode(volunteer.getPassword()));
        }        
        existing.setPhone(volunteer.getPhone());
        existing.setRole(volunteer.getRole());
        existing.setVillage(volunteer.getVillage());
        return volunteerRepository.save(existing);
    }

    @Transactional
    public void deleteVolunteer(Long id) {
        if (!volunteerRepository.existsById(id)) {
            throw new EntityNotFoundException("Volunteer not found");
        }
        volunteerRepository.deleteById(id);
    }

    public Page<Volunteer> getAllVolunteers(Pageable pageable) {
        return volunteerRepository.findAll(pageable);
    }

    public Page<Volunteer> searchVolunteers(String query, Pageable pageable) {
        String lowerQuery = query.toLowerCase();
        List<Volunteer> filtered = volunteerRepository.findAll().stream()
                .filter(v -> v.getFirstName().toLowerCase().contains(lowerQuery) ||
                           v.getLastName().toLowerCase().contains(lowerQuery) ||
                           v.getEmail().toLowerCase().contains(lowerQuery) ||
                           (v.getPhone() != null && v.getPhone().contains(query)))
                .collect(Collectors.toList());
        
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), filtered.size());
        List<Volunteer> pageContent = filtered.subList(start, end);
        
        return new PageImpl<>(pageContent, pageable, filtered.size());
    }

    public Page<Volunteer> getVolunteersByVillage(Long villageId, Pageable pageable) {
        return volunteerRepository.findByVillage_LocationId(villageId, pageable);
    }

    public List<Volunteer> getVolunteersByProvinceCode(String code) {
        return volunteerRepository.findByProvinceCode(code);
    }

    public List<Volunteer> getVolunteersByProvinceName(String name) {
        return volunteerRepository.findByProvinceName(name);
    }
}