package com.fast.fsf.campusmap.state;

import com.fast.fsf.campusmap.domain.CampusLocation;

/**
 * Context class for location moderation, delegating operations to the current state.
 */
public class LocationModerationContext {

    private final LocationModerationState state;

    public LocationModerationContext(CampusLocation location) {
        if (location.isFlagged()) {
            this.state = new FlaggedLocationState();
        } else if (location.isApproved()) {
            this.state = new ApprovedLocationState();
        } else {
            this.state = new PendingLocationState();
        }
    }

    public void approve(CampusLocation location, String reason) {
        state.approve(location, reason);
    }

    public void flag(CampusLocation location, String reason) {
        state.flag(location, reason);
    }

    public void resolveFlag(CampusLocation location) {
        state.resolveFlag(location);
    }

    public String getStateName() {
        return state.getStateName();
    }
}
