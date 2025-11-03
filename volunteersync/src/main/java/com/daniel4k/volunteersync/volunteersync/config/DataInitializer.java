package com.daniel4k.volunteersync.volunteersync.config;

import com.daniel4k.volunteersync.volunteersync.model.Location;
import com.daniel4k.volunteersync.volunteersync.model.LocationType;
import com.daniel4k.volunteersync.volunteersync.model.Role;
import com.daniel4k.volunteersync.volunteersync.model.Volunteer;
import com.daniel4k.volunteersync.volunteersync.repository.LocationRepository;
import com.daniel4k.volunteersync.volunteersync.repository.VolunteerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final VolunteerRepository volunteerRepository;
    private final LocationRepository locationRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(VolunteerRepository volunteerRepository,
            LocationRepository locationRepository,
            PasswordEncoder passwordEncoder) {
        this.volunteerRepository = volunteerRepository;
        this.locationRepository = locationRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // Check if test province already exists
        Location province = locationRepository.findByCode("TP");
        if (province == null) {
            province = new Location();
            province.setName("Test Province");
            province.setCode("TP");
            province.setType(LocationType.PROVINCE);
            province.setParent(null);
            province = locationRepository.save(province);
            System.out.println("Created Test Province");
        } else {
            System.out.println("Test Province already exists");
        }

        // Check if test district already exists
        Location district = locationRepository.findByCode("TD");
        if (district == null) {
            district = new Location();
            district.setName("Test District");
            district.setCode("TD");
            district.setType(LocationType.DISTRICT);
            district.setParent(province);
            district = locationRepository.save(district);
            System.out.println("Created Test District");
        } else {
            System.out.println("Test District already exists");
        }

        // Check if test sector already exists
        Location sector = locationRepository.findByCode("TS");
        if (sector == null) {
            sector = new Location();
            sector.setName("Test Sector");
            sector.setCode("TS");
            sector.setType(LocationType.SECTOR);
            sector.setParent(district);
            sector = locationRepository.save(sector);
            System.out.println("Created Test Sector");
        } else {
            System.out.println("Test Sector already exists");
        }

        // Check if test cell already exists
        Location cell = locationRepository.findByCode("TC");
        if (cell == null) {
            cell = new Location();
            cell.setName("Test Cell");
            cell.setCode("TC");
            cell.setType(LocationType.CELL);
            cell.setParent(sector);
            cell = locationRepository.save(cell);
            System.out.println("Created Test Cell");
        } else {
            System.out.println("Test Cell already exists");
        }

        // Check if test village already exists
        Location village = locationRepository.findByCode("TV");
        if (village == null) {
            village = new Location();
            village.setName("Test Village");
            village.setCode("TV");
            village.setType(LocationType.VILLAGE);
            village.setParent(cell);
            village = locationRepository.save(village);
            System.out.println("Created Test Village");
        } else {
            System.out.println("Test Village already exists");
        }

        // Create NGO_ADMIN volunteer
        if (volunteerRepository.findByEmail("admin@ngo.com") == null) {
            Volunteer admin = new Volunteer();
            admin.setFirstName("NGO");
            admin.setLastName("Admin");
            admin.setEmail("admin@ngo.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setPhone("+250788123456");
            admin.setRole(Role.NGO_ADMIN);
            admin.setVillage(village);
            volunteerRepository.save(admin);
            System.out.println("Created NGO Admin: admin@ngo.com / admin123");
        } else {
            System.out.println("NGO Admin already exists");
        }

        // Create regular VOLUNTEER
        if (volunteerRepository.findByEmail("volunteer@test.com") == null) {
            Volunteer volunteer = new Volunteer();
            volunteer.setFirstName("Test");
            volunteer.setLastName("Volunteer");
            volunteer.setEmail("volunteer@test.com");
            volunteer.setPassword(passwordEncoder.encode("volunteer123"));
            volunteer.setPhone("+250788654321");
            volunteer.setRole(Role.VOLUNTEER);
            volunteer.setVillage(village);
            volunteerRepository.save(volunteer);
            System.out.println("Created Volunteer: volunteer@test.com / volunteer123");
        } else {
            System.out.println("Volunteer already exists");
        }
    }
}