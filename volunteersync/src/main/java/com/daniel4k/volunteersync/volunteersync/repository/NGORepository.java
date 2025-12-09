package com.daniel4k.volunteersync.volunteersync.repository;

import com.daniel4k.volunteersync.volunteersync.model.NGO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NGORepository extends JpaRepository<NGO, Long> {
    boolean existsByContactEmail(String contactEmail);
@Query("SELECT n FROM NGO n WHERE " +
       "LOWER(n.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
       "LOWER(n.description) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
       "LOWER(n.contactEmail) LIKE LOWER(CONCAT('%', :query, '%'))")
Page<NGO> searchNGOs(@Param("query") String query, Pageable pageable);

}
