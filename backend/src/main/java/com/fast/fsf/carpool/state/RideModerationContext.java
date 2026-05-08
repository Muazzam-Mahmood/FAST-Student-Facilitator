package com.fast.fsf.carpool.state;

import com.fast.fsf.carpool.domain.Ride;

/**
 * Context class for ride moderation, delegating operations to the current state.
 */
public final class RideModerationContext {

    private final Ride ride;

    public RideModerationContext(Ride ride) {
        this.ride = ride;
    }

    public Ride getRide() {
        return ride;
    }

    public void approve(String moderationReasonOrNull) {
        RideModerationStates.fromRide(ride).approve(this, moderationReasonOrNull);
    }

    public void flag(String flagReasonOrNull) {
        RideModerationStates.fromRide(ride).flag(this, flagReasonOrNull);
    }

    public void resolve() {
        RideModerationStates.fromRide(ride).resolve(this);
    }
}
