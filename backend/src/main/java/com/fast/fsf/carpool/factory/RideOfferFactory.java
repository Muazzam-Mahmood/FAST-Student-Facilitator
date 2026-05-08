package com.fast.fsf.carpool.factory;

import com.fast.fsf.carpool.domain.Ride;
import org.springframework.stereotype.Component;

/**
 * Factory for creating and validating ride offers.
 * Enforces business invariants such as seat limits and checkpoint counts.
 */
@Component
public class RideOfferFactory {

    private static final int MAX_CHECKPOINTS = 5;
    private static final int MIN_SEATS = 1;
    private static final int MAX_SEATS = 4;

    /**
     * Factory method: validates and normalises an incoming listing before persistence.
     *
     * @throws IllegalArgumentException when seats are outside 1–4 or checkpoint cap is violated (HTTP 400 in controller).
     */
    public Ride createPendingListing(Ride incoming) {
        if (incoming.getAvailableSeats() < MIN_SEATS || incoming.getAvailableSeats() > MAX_SEATS) {
            throw new IllegalArgumentException(
                    "Available seats must be between " + MIN_SEATS + " and " + MAX_SEATS + ".");
        }
        if (incoming.getCheckpoints() != null && incoming.getCheckpoints().size() > MAX_CHECKPOINTS) {
            throw new IllegalArgumentException("Maximum of " + MAX_CHECKPOINTS + " checkpoints allowed.");
        }
        incoming.setApproved(false);
        incoming.setFlagged(false);
        return incoming;
    }
}
