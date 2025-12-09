package com.daniel4k.volunteersync.volunteersync.repository;

import com.daniel4k.volunteersync.volunteersync.model.Opportunity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OpportunityRepository extends JpaRepository<Opportunity, Long> {
    @Query("SELECT o FROM Opportunity o WHERE o.ngo.ngoId = :ngoId")
    Page<Opportunity> findByNgoId(@Param("ngoId") Long ngoId, Pageable pageable);

    @Query("SELECT o FROM Opportunity o WHERE o.village.locationId = :villageId")
    Page<Opportunity> findByVillageId(@Param("villageId") Long villageId, Pageable pageable);

    @Query("SELECT o FROM Opportunity o WHERE " +
       "LOWER(o.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
       "LOWER(o.description) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
       "LOWER(o.ngo.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
       "LOWER(o.village.name) LIKE LOWER(CONCAT('%', :query, '%'))")
Page<Opportunity> searchOpportunities(@Param("query") String query, Pageable pageable);

}
