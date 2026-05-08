package com.fast.fsf.campusmap.service;

import com.fast.fsf.campusmap.adapter.ApprovedLocationCatalog;
import com.fast.fsf.campusmap.criterion.LocationSearchCriterion;
import com.fast.fsf.campusmap.domain.CampusLocation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for searching campus locations based on specific criteria.
 */
@Service
public class LocationSearchService {

    private final ApprovedLocationCatalog locationCatalog;

    @Autowired
    public LocationSearchService(ApprovedLocationCatalog locationCatalog) {
        this.locationCatalog = locationCatalog;
    }

    public List<CampusLocation> search(LocationSearchCriterion criterion) {
        return locationCatalog.findAllApproved().stream()
                .filter(criterion::matches)
                .collect(Collectors.toList());
    }
}
