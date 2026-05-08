package com.fast.fsf.config;

import com.fast.fsf.identity.domain.User;
import com.fast.fsf.identity.persistence.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * Intercepts incoming requests and populates the SecurityContext based on custom headers.
 * Validates user ban status and assigns authorities based on the provided role.
 */
@Component
public class PreAuthFilter extends OncePerRequestFilter {

    @Autowired
    private UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String email = request.getHeader("X-User-Email");
        String roleHeader = request.getHeader("X-User-Role");

        if (email != null && !email.isBlank()) {
            User user = userRepository.findByEmail(email);
            
            // If user is banned, block the request immediately
            if (user != null && user.isBanned()) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.getWriter().write("YOUR_ACCOUNT_IS_BANNED");
                return;
            }

            // Trust the header for role if user is found, otherwise use header role
            String role = (user != null) ? user.getRole().name() : (roleHeader != null ? roleHeader : "STUDENT");
            String authority = role.startsWith("ROLE_") ? role : "ROLE_" + role;
            
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                    email, null, Collections.singletonList(new SimpleGrantedAuthority(authority))
            );
            
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        filterChain.doFilter(request, response);
    }
}
