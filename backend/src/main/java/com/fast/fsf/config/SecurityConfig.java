package com.fast.fsf.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * Security configuration for the application.
 * Configures CORS, CSRF, and authorization rules for REST endpoints.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final PreAuthFilter preAuthFilter;

    public SecurityConfig(PreAuthFilter preAuthFilter) {
        this.preAuthFilter = preAuthFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // 1. Disable CSRF for development (so our React app can POST data)
            .csrf(csrf -> csrf.disable())
            
            // Enable CORS (allowing our React app on port 5173 to talk to Java)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))

            .addFilterBefore(preAuthFilter, org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class)

            // Define which pages are public and which are private
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/rides/**").permitAll() // Students can view/post rides
                .requestMatchers("/api/past-papers/**").permitAll()
                .requestMatchers("/api/users/**").permitAll()
                .requestMatchers("/api/lost-found/**").permitAll()
                .requestMatchers("/api/notes/**").permitAll()
                .requestMatchers("/api/timetable/**").permitAll()
                .requestMatchers("/api/books/**").permitAll()
                .requestMatchers("/api/events/**").permitAll()
                .requestMatchers("/api/reminders/**").permitAll()
                .requestMatchers("/api/campus-map/**").permitAll()
                .anyRequest().authenticated()
            );

            /* 
            // 4. Configure OAuth2 Login (Google)
            .oauth2Login(oauth2 -> oauth2
                .defaultSuccessUrl("http://localhost:5173/", true)
            );
            */

        return http.build();
    }

    /**
     * CORS Configuration
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
