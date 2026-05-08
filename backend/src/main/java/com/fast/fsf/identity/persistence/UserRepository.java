package com.fast.fsf.identity.persistence;

import com.fast.fsf.identity.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for managing User entities.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * Finds a user by their email address.
     *
     * @param email the email address to search for
     * @return the user if found, or null otherwise
     */
    User findByEmail(String email);
}
