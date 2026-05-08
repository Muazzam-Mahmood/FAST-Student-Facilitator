package com.fast.fsf.campusmap.observer;

import com.fast.fsf.campusmap.domain.CampusLocation;
import com.fast.fsf.campusmap.domain.CampusMapRoute;
import com.fast.fsf.campusmap.domain.LocationSuggestion;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Publisher for campus map events.
 * Dispatches events to all registered MapEventListener implementations.
 */
@Service
public class MapEventPublisher {

    private final List<MapEventListener> listeners = new ArrayList<>();

    public MapEventPublisher(List<MapEventListener> listeners) {
        this.listeners.addAll(listeners);
    }

    public void publishLocationAdded(CampusLocation location) {
        listeners.forEach(l -> l.onLocationAdded(location));
    }

    public void publishLocationApproved(CampusLocation location) {
        listeners.forEach(l -> l.onLocationApproved(location));
    }

    public void publishLocationFlagged(CampusLocation location, String reason) {
        listeners.forEach(l -> l.onLocationFlagged(location, reason));
    }

    public void publishLocationDeleted(CampusLocation location, String reason) {
        listeners.forEach(l -> l.onLocationDeleted(location, reason));
    }

    public void publishRouteStepAdded(CampusMapRoute step) {
        listeners.forEach(l -> l.onRouteStepAdded(step));
    }

    public void publishRouteDeleted(String from, String to) {
        listeners.forEach(l -> l.onRouteDeleted(from, to));
    }

    public void publishDirectionsRequested(String from, String to) {
        listeners.forEach(l -> l.onDirectionsRequested(from, to));
    }

    public void publishSuggestionSubmitted(LocationSuggestion suggestion) {
        listeners.forEach(l -> l.onSuggestionSubmitted(suggestion));
    }

    public void publishSuggestionResolved(LocationSuggestion suggestion) {
        listeners.forEach(l -> l.onSuggestionResolved(suggestion));
    }
}
