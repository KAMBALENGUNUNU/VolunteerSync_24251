# VolunteerSync - Volunteer Management System

## 📋 Project Overview

VolunteerSync is a comprehensive Spring Boot-based volunteer management system designed to connect NGOs with volunteers in Rwanda. The platform demonstrates mastery of Spring Boot web technologies while solving real-world volunteer coordination challenges across Rwanda's administrative structure.

## 🎯 Project Implementation Requirements Fulfillment

### ✅ **Requirement 1: Project Structure with 5+ Entities**

Our project exceeds this requirement with **5 well-defined entities**, each implementing complete CRUD operations:

| Entity | CRUD Endpoints | Description |
|--------|----------------|-------------|
| **Volunteer** | `POST /api/volunteers`<br>`GET /api/volunteers/{id}`<br>`PUT /api/volunteers/{id}`<br>`DELETE /api/volunteers/{id}` | System users with role-based access |
| **NGO** | `POST /api/ngos`<br>`GET /api/ngos/{id}`<br>`PUT /api/ngos/{id}`<br>`DELETE /api/ngos/{id}` | Organizations posting opportunities |
| **Opportunity** | `POST /api/opportunities`<br>`GET /api/opportunities/{id}`<br>`PUT /api/opportunities/{id}`<br>`DELETE /api/opportunities/{id}` | Volunteer tasks/events |
| **Application** | `POST /api/applications/apply`<br>`GET /api/applications/{id}`<br>`PATCH /api/applications/{id}/status`<br>`DELETE /api/applications/{id}` | Volunteer opportunity applications |
| **Location** | `POST /api/locations`<br>`GET /api/locations/{id}`<br>`PUT /api/locations/{id}`<br>`DELETE /api/locations/{id}` | Rwandan administrative hierarchy |

### ✅ **Requirement 2: Spring Data JPA Repository Methods**

Our implementation comprehensively demonstrates Spring Data JPA capabilities:

#### **Derived Query Methods:**
```java
// VolunteerRepository
Volunteer findByEmail(String email);
boolean existsByEmail(String email);
Page<Volunteer> findByVillage_LocationId(Long villageId, Pageable pageable);

// ApplicationRepository  
boolean existsByVolunteer_VolunteerIdAndOpportunity_OpportunityId(Long volunteerId, Long opportunityId);
Page<Application> findByVolunteer_VolunteerId(Long volunteerId, Pageable pageable);
Page<Application> findByOpportunity_OpportunityId(Long opportunityId, Pageable pageable);

// LocationRepository
Location findByCode(String code);
boolean existsByCode(String code);
Location findByNameAndType(String name, LocationType type);

// NGORepository
boolean existsByContactEmail(String contactEmail);
```
#### **Custom Query Methods with @Query:**

```java
// Complex hierarchical queries in VolunteerRepository
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

// OpportunityRepository with custom JPQL
@Query("SELECT o FROM Opportunity o WHERE o.ngo.ngoId = :ngoId")
Page<Opportunity> findByNgoId(@Param("ngoId") Long ngoId, Pageable pageable);
```

#### **Sorting and Pagination Implementation:**
All list endpoints support Spring Data's `Pageable` interface:

```java
// Controller examples
@GetMapping("/by-volunteer/{volunteerId}")
public ResponseEntity<Page<Application>> byVolunteer(@PathVariable Long volunteerId, Pageable pageable) {
    return ResponseEntity.ok(service.byVolunteer(volunteerId, pageable));
}

// Usage: /api/applications/by-volunteer/1?page=0&size=10&sort=appliedDate,desc
```

#### **Requirement 3: Rwandan Location Table Implementation**
Our Location entity accurately models Rwanda's 5-level administrative hierarchy:

Entity Structure:

```java
@Entity
public class Location {
    private Long locationId;
    private String name;
    private String code;        // Unique administrative codes
    private LocationType type;  // PROVINCE, DISTRICT, SECTOR, CELL, VILLAGE
    private Location parent;    // Self-referencing for hierarchy
}
```
Hierarchy Validation:

```java
@Service
public class LocationService {
    // Enforces Rwandan administrative rules:
    // - PROVINCE must not have parent (top-level)
    // - DISTRICT → SECTOR → CELL → VILLAGE must have valid parents
    // - Prevents invalid hierarchical relationships
}
```

Complete Administrative Coverage:
5 Provinces: Northern, Southern, Eastern, Western, Kigali

30 Districts

416 Sectors

2,148 Cells

14,837 Villages

#### **Requirement 4: User-Location Relationship & Province-based Queries**

User-Location Relationship:

```java
@Entity
public class Volunteer {
    @NotNull 
    @ManyToOne 
    @JoinColumn(name = "village_id", nullable = false)
    private Location village;  // Each volunteer belongs to a specific village
}
```
Province-based API Endpoints:

```java
@RestController
public class VolunteerController {
    
    @GetMapping("/by-province")
    public ResponseEntity<List<Volunteer>> getVolunteersByProvince(
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String name) {
        
        if (code != null) {
            return ResponseEntity.ok(volunteerService.getVolunteersByProvinceCode(code));
        } else if (name != null) {
            return ResponseEntity.ok(volunteerService.getVolunteersByProvinceName(name));
        }
        return ResponseEntity.badRequest().build();
    }
}
```
**Bidirectional Location Navigation:**
From Volunteer → find their Village → trace up to Province

From Province → find all Volunteers in that province

Hierarchical queries traverse: Province ← District ← Sector ← Cell ← Village ← Volunteer

#### **Requirement 5: Three Relationship Types Implementation**

**1. One-to-One Relationship:**
   
```java
// NGO to Volunteer (Admin relationship)
@Entity
public class NGO {
    @OneToOne 
    @JoinColumn(name = "admin_id", unique = true, nullable = false)
    private Volunteer admin;  // Each NGO has exactly one admin
}
```
**One-to-Many / Many-to-One Relationships:**
   
```java
// NGO to Opportunity (One-to-Many)
@Entity
public class NGO {
    // One NGO posts many Opportunities
}

@Entity  
public class Opportunity {
    @ManyToOne
    @JoinColumn(name = "ngo_id")
    private NGO ngo;  // Many Opportunities belong to one NGO
}

// Volunteer to Application (One-to-Many)
@Entity
public class Volunteer {
    // One Volunteer submits many Applications  
}

@Entity
public class Application {
    @ManyToOne
    @JoinColumn(name = "volunteer_id")
    private Volunteer volunteer;  // Many Applications belong to one Volunteer
}
```
**Many-to-Many Relationship:**
   
```java
// Volunteer to Opportunity via Application (join table with additional attributes)
@Entity
public class Application {
    @ManyToOne
    private Volunteer volunteer;     // Many Applications from many Volunteers
    
    @ManyToOne  
    private Opportunity opportunity; // Many Applications for many Opportunities
    
    // Additional attributes in join table:
    private ApplicationStatus status;
    private LocalDateTime appliedDate;
}
```
**Self-Referencing Relationship:**
   
```java
// Location hierarchy (One-to-Many within same entity)
@Entity
public class Location {
    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Location parent;  // One parent has many children
    
    // One Location (Province) has many child Locations (Districts)
    // One Location (District) has many child Locations (Sectors), etc.
}
```
##🏗️ Technical Architecture

##🔄 Data Flow Sequence Diagram

