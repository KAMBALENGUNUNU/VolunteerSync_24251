package com.daniel4k.volunteersync.volunteersync.repository;

import com.daniel4k.volunteersync.volunteersync.model.Volunteer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface VolunteerRepository extends JpaRepository<Volunteer, Long> {
    Volunteer findByEmail(String email);

    boolean existsByEmail(String email);

    Page<Volunteer> findByVillage_LocationId(Long villageId, Pageable pageable);

    @Query("""
            SELECT v FROM Volunteer v
            JOIN v.village vill
            JOIN vill.parent cell
            JOIN cell.parent sector
            JOIN sector.parent district
            JOIN district.parent province
            WHERE province.code = :code
            """)
    List<Volunteer> findByProvinceCode(@Param("code") String code);

    @Query("""
            SELECT v FROM Volunteer v
            JOIN v.village vill
            JOIN vill.parent cell
            JOIN cell.parent sector
            JOIN sector.parent district
            JOIN district.parent province
            WHERE province.name = :name
            """)
    List<Volunteer> findByProvinceName(@Param("name") String name);
    @Query("SELECT v FROM Volunteer v WHERE " +
       "LOWER(v.firstName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
       "LOWER(v.lastName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
       "LOWER(v.email) LIKE LOWER(CONCAT('%', :query, '%'))")
Page<Volunteer> searchVolunteers(@Param("query") String query, Pageable pageable);

}
