package com.daniel4k.volunteersync.volunteersync.repository;

import com.daniel4k.volunteersync.volunteersync.model.NGO;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NGORepository extends JpaRepository<NGO, Long> {
    boolean existsByContactEmail(String contactEmail);
}
