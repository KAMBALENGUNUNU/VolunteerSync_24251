package com.daniel4k.volunteersync.volunteersync.service;

import com.daniel4k.volunteersync.volunteersync.model.Volunteer;
import com.daniel4k.volunteersync.volunteersync.repository.VolunteerRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final VolunteerRepository volunteerRepository;

    public UserDetailsServiceImpl(VolunteerRepository volunteerRepository) {
        this.volunteerRepository = volunteerRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Volunteer volunteer = volunteerRepository.findByEmail(email);
        if (volunteer == null) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }

        return User.builder().username(volunteer.getEmail()).password(volunteer.getPassword()).authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + volunteer.getRole().name()))).build();
    }
}