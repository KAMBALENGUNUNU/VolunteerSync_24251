package com.daniel4k.volunteersync.volunteersync.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
          // 1. Disable CSRF (using JWTs)
          .csrf(csrf -> csrf.disable())
          
          // 2. ENABLE CORS (Critical for Frontend communication)
          .cors(cors -> cors.configurationSource(corsConfigurationSource()))
          
          // 3. Stateless Sessions
          .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
          
          // 4. Authorization Rules
          .authorizeHttpRequests(auth -> auth
              // A. Public Auth Endpoints
              .requestMatchers(
                  "/api/auth/register",
                  "/api/auth/login",
                  "/api/auth/verify-2fa",
                  "/api/auth/forgot-password",
                  "/api/auth/reset-password"
              ).permitAll()
              
              // B. PROTECTED Auth Endpoint (Critical for User Profile)
              .requestMatchers("/api/auth/me").authenticated() 

              // C. Public Location Dropdowns (For Registration)
              .requestMatchers("/api/locations/provinces", "/api/locations/children/**").permitAll()

              // D. Secure Everything Else
              .requestMatchers(
                  "/api/volunteers/**",
                  "/api/ngos/**",
                  "/api/opportunities/**",
                  "/api/applications/**",
                  "/api/locations/**",
                  "/api/dashboard/**",
                  "/api/search/**"
              ).authenticated()
              
              .anyRequest().permitAll()
          )
          .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    // --- NEW: CORS Configuration Bean ---
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // 1. Allow Frontend Origins (Adjust port if your React app runs on 5173 or 3000)
        configuration.setAllowedOrigins(List.of("http://localhost:3000", "http://localhost:5173")); 
        
        // 2. Allow HTTP Methods
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        
        // 3. Allow Headers (Authorization, Content-Type, etc.)
        configuration.setAllowedHeaders(List.of("*"));
        
        // 4. Allow Credentials (for cookies/tokens if needed in future)
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() { 
        return new BCryptPasswordEncoder(); 
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}