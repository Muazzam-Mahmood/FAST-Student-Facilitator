package com.fast.fsf.campusmap.state;

import com.fast.fsf.campusmap.domain.CampusLocation;

/**
 * Interface for location moderation states.
 * Encapsulates the behavior for approving, flagging, and resolving flags on campus locations.
 */
public interface LocationModerationState {
    void approve(CampusLocation location, String reason);
    void flag(CampusLocation location, String reason);
    void resolveFlag(CampusLocation location);
    String getStateName();
}
