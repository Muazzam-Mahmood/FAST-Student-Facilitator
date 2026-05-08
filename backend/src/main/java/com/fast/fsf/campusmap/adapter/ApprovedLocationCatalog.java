package com.fast.fsf.campusmap.adapter;

import com.fast.fsf.campusmap.domain.CampusLocation;
import java.util.List;
import java.util.Optional;

/**
 * Interface for providing a source of approved campus locations.
 */
public interface ApprovedLocationCatalog {
    List<CampusLocation> findAllApproved();
    List<CampusLocation> searchLocations(String query);
    Optional<CampusLocation> findApprovedByName(String name);
    List<CampusLocation> findByCategory(String category);
}
