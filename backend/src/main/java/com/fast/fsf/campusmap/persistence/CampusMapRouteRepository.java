package com.fast.fsf.campusmap.persistence;

import com.fast.fsf.campusmap.domain.CampusMapRoute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for managing CampusMapRoute entities.
 */
@Repository
public interface CampusMapRouteRepository extends JpaRepository<CampusMapRoute, Long> {

    // Route lookup — ordered
    List<CampusMapRoute> findByFromLocationAndToLocationOrderByStepOrderAsc(
            String fromLocation, String toLocation);

    // Route existence check
    boolean existsByFromLocationAndToLocation(String fromLocation, String toLocation);

    // Admin route management
    List<CampusMapRoute> findByFromLocationAndToLocation(
            String fromLocation, String toLocation);

    // Admin Panel / Stats support
    long countByApprovedTrue();
    long countByFlaggedTrue();
    List<CampusMapRoute> findByApprovedFalse();
    List<CampusMapRoute> findByFlaggedTrue();
}
