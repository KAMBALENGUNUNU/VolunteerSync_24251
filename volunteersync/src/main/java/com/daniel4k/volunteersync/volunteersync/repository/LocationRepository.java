package com.daniel4k.volunteersync.volunteersync.repository;

import com.daniel4k.volunteersync.volunteersync.model.Location;
import com.daniel4k.volunteersync.volunteersync.model.LocationType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationRepository extends JpaRepository<Location, Long> {
    Location findByCode(String code);

    boolean existsByCode(String code);

    Location findByNameAndType(String name, LocationType type);
}
