package com.fast.fsf.carpool.event;

import org.springframework.context.ApplicationEvent;

/**
 * Event published when a ride is flagged by an administrator.
 */
public class RideFlaggedEvent extends ApplicationEvent {

    private final Long rideId;
    private final String flagReasonOrNull;

    public RideFlaggedEvent(Object source, Long rideId, String flagReasonOrNull) {
        super(source);
        this.rideId = rideId;
        this.flagReasonOrNull = flagReasonOrNull;
    }

    public Long getRideId() {
        return rideId;
    }

    public String getFlagReasonOrNull() {
        return flagReasonOrNull;
    }
}
