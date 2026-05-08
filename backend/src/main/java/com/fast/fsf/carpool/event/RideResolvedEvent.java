package com.fast.fsf.carpool.event;

import org.springframework.context.ApplicationEvent;

/**
 * Event published when a flagged ride is resolved (flag cleared).
 */
public class RideResolvedEvent extends ApplicationEvent {

    private final Long rideId;

    public RideResolvedEvent(Object source, Long rideId) {
        super(source);
        this.rideId = rideId;
    }

    public Long getRideId() {
        return rideId;
    }
}
