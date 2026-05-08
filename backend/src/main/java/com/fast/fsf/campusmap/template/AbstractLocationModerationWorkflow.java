package com.fast.fsf.campusmap.template;

import com.fast.fsf.campusmap.domain.CampusLocation;
import com.fast.fsf.campusmap.observer.MapEventPublisher;
import com.fast.fsf.campusmap.persistence.CampusLocationRepository;

/**
 * Abstract workflow for moderating campus locations.
 * Defines the algorithm for loading, validating, applying changes, and notifying observers.
 */
public abstract class AbstractLocationModerationWorkflow {

    protected final CampusLocationRepository locationRepository;
    protected final MapEventPublisher eventPublisher;

    protected AbstractLocationModerationWorkflow(CampusLocationRepository locationRepository, MapEventPublisher eventPublisher) {
        this.locationRepository = locationRepository;
        this.eventPublisher = eventPublisher;
    }

    public final CampusLocation execute(Long locationId, String reason) {
        CampusLocation loc = findLocation(locationId);
        validateTransition(loc, reason);
        applyChange(loc, reason);
        CampusLocation saved = locationRepository.save(loc);
        publishEvent(saved, reason);
        return saved;
    }

    protected CampusLocation findLocation(Long id) {
        return locationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Location not found with ID: " + id));
    }

    protected abstract void validateTransition(CampusLocation location, String reason);
    protected abstract void applyChange(CampusLocation location, String reason);
    protected abstract void publishEvent(CampusLocation location, String reason);
}
