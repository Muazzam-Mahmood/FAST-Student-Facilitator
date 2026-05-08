package com.fast.fsf.carpool.persistence;

import com.fast.fsf.carpool.domain.Ride;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Repository for managing Ride entities.
 */
@Repository
public interface RideRepository extends JpaRepository<Ride, Long> {
    
    // Custom moderation queries
    long countByFlaggedTrue();
    List<Ride> findByFlaggedTrue();
    
    // Approval filtering
    long countByApprovedTrue();
    List<Ride> findByApprovedTrue();
    List<Ride> findByApprovedFalse();
    long countByApprovedFalse();
    
    List<Ride> findByDestinationContainingIgnoreCaseAndApprovedTrue(String destination);
    List<Ride> findByOriginContainingIgnoreCaseAndApprovedTrue(String origin);

    @org.springframework.data.jpa.repository.Query("SELECT r FROM Ride r WHERE r.approved = true OR r.driverEmail = :driverEmail")
    List<Ride> findAllVisibleToUser(@org.springframework.data.repository.query.Param("driverEmail") String driverEmail);
}
