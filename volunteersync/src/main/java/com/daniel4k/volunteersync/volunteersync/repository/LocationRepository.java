package com.daniel4k.volunteersync.volunteersync.repository;

import com.daniel4k.volunteersync.volunteersync.model.Location;
import com.daniel4k.volunteersync.volunteersync.model.LocationType;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LocationRepository extends JpaRepository<Location, Long> {
    Location findByCode(String code);

    boolean existsByCode(String code);

    Location findByNameAndType(String name, LocationType type);
    // 1. To fetch the top level (Provinces)
    List<Location> findByType(LocationType type);

    // 2. To fetch the children (Districts, Sectors, Cells, Villages)
    List<Location> findByParent_LocationId(Long parentId);

}
