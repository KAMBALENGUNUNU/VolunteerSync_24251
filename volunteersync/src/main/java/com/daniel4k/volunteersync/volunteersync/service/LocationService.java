package com.daniel4k.volunteersync.volunteersync.service;

import com.daniel4k.volunteersync.volunteersync.model.Location;
import com.daniel4k.volunteersync.volunteersync.model.LocationType;
import com.daniel4k.volunteersync.volunteersync.repository.LocationRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LocationService {
    @Autowired
    private LocationRepository locationRepository;

    @Transactional
    public Location createLocation(@Valid Location location) {
        if (locationRepository.existsByCode(location.getCode())) {
            throw new IllegalArgumentException("Code already exists");
        }
        if (location.getParent() != null && !locationRepository.existsById(location.getParent().getLocationId())) {
            throw new IllegalArgumentException("Invalid parent location");
        }
        if (location.getType() == LocationType.PROVINCE && location.getParent() != null)
            throw new IllegalArgumentException("Province must not have a parent");
        if (location.getType() != LocationType.PROVINCE && location.getParent() == null)
            throw new IllegalArgumentException(location.getType() + " must have a parent");
        return locationRepository.save(location);
    }

    public Location getLocation(Long id) {
        return locationRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Location not found"));
    }

    @Transactional
    public Location updateLocation(Long id, @Valid Location location) {
        Location existing = getLocation(id);
        if (!existing.getCode().equals(location.getCode()) && locationRepository.existsByCode(location.getCode())) {
            throw new IllegalArgumentException("Code already exists");
        }
        if (location.getType() == LocationType.PROVINCE && location.getParent() != null)
            throw new IllegalArgumentException("Province must not have a parent");
        if (location.getType() != LocationType.PROVINCE && location.getParent() == null)
            throw new IllegalArgumentException(location.getType() + " must have a parent");
        existing.setName(location.getName());
        existing.setCode(location.getCode());
        existing.setType(location.getType());
        existing.setParent(location.getParent());
        return locationRepository.save(existing);
    }

    @Transactional
    public void deleteLocation(Long id) {
        if (!locationRepository.existsById(id)) {
            throw new EntityNotFoundException("Location not found");
        }
        locationRepository.deleteById(id);
    }
}