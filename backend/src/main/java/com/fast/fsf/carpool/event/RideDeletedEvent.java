package com.fast.fsf.carpool.event;

import org.springframework.context.ApplicationEvent;

/**
 * Event published when a ride is deleted.
 */
public class RideDeletedEvent extends ApplicationEvent {

    private final Long rideId;
    private final String deletionReasonOrNull;

    public RideDeletedEvent(Object source, Long rideId, String deletionReasonOrNull) {
        super(source);
        this.rideId = rideId;
        this.deletionReasonOrNull = deletionReasonOrNull;
    }

    public Long getRideId() {
        return rideId;
    }

    public String getDeletionReasonOrNull() {
        return deletionReasonOrNull;
    }
}
