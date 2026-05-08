package com.fast.fsf.carpool.event;

import com.fast.fsf.carpool.domain.Ride;
import org.springframework.context.ApplicationEvent;

/**
 * Event published when a new ride is offered.
 */
public class RideOfferedEvent extends ApplicationEvent {

    private final Ride savedRide;

    public RideOfferedEvent(Object source, Ride savedRide) {
        super(source);
        this.savedRide = savedRide;
    }

    public Ride getSavedRide() {
        return savedRide;
    }
}
