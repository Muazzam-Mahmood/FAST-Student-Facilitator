package com.fast.fsf.campusmap.observer;

import com.fast.fsf.campusmap.domain.CampusLocation;
import com.fast.fsf.campusmap.domain.CampusMapRoute;
import com.fast.fsf.campusmap.domain.LocationSuggestion;
import com.fast.fsf.shared.domain.ActivityLog;
import com.fast.fsf.shared.persistence.ActivityLogRepository;
import org.springframework.stereotype.Component;

@Component
public class MapActivityLogObserver implements MapEventListener {

    private final ActivityLogRepository activityLogRepo;

    public MapActivityLogObserver(ActivityLogRepository activityLogRepo) {
        this.activityLogRepo = activityLogRepo;
    }

    @Override
    public void onLocationAdded(CampusLocation location) {
        activityLogRepo.save(new ActivityLog("LOCATION_ADDED", 
            "Location added: " + location.getLocationName(), location.getOwnerEmail()));
    }

    @Override
    public void onLocationApproved(CampusLocation location) {
        activityLogRepo.save(new ActivityLog("LOCATION_APPROVED", 
            "Location approved: " + location.getLocationName(), "admin@nu.edu.pk"));
    }

    @Override
    public void onLocationFlagged(CampusLocation location, String reason) {
        activityLogRepo.save(new ActivityLog("LOCATION_FLAGGED", 
            "Location flagged: " + location.getLocationName() + " Reason: " + reason, "admin@nu.edu.pk"));
    }

    @Override
    public void onLocationDeleted(CampusLocation location, String reason) {
        activityLogRepo.save(new ActivityLog("LOCATION_DELETED", 
            "Location deleted: " + location.getLocationName() + " Reason: " + reason, "admin@nu.edu.pk"));
    }

    @Override
    public void onRouteStepAdded(CampusMapRoute step) {
        activityLogRepo.save(new ActivityLog("ROUTE_STEP_ADDED", 
            "Route step added: " + step.getFromLocation() + " -> " + step.getToLocation(), step.getOwnerEmail()));
    }

    @Override
    public void onRouteDeleted(String from, String to) {
        activityLogRepo.save(new ActivityLog("ROUTE_DELETED", 
            "Route deleted: " + from + " -> " + to, "admin@nu.edu.pk"));
    }

    @Override
    public void onDirectionsRequested(String from, String to) {
        activityLogRepo.save(new ActivityLog("DIRECTIONS_REQUESTED", 
            "Directions from " + from + " to " + to, "student@nu.edu.pk"));
    }

    @Override
    public void onSuggestionSubmitted(LocationSuggestion suggestion) {
        activityLogRepo.save(new ActivityLog("SUGGESTION_SUBMITTED", 
            "Suggestion submitted: " + suggestion.getLocationName(), suggestion.getSubmittedBy()));
    }

    @Override
    public void onSuggestionResolved(LocationSuggestion suggestion) {
        activityLogRepo.save(new ActivityLog("SUGGESTION_RESOLVED", 
            "Suggestion resolved: " + suggestion.getLocationName(), "admin@nu.edu.pk"));
    }
}
