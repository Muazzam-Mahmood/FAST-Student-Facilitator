package com.fast.fsf.search.criterion;

import com.fast.fsf.carpool.domain.Ride;

import java.util.Locale;
import java.util.Objects;

/**
 * Criterion for filtering rides by destination substring.
 */
public final class DestinationContainsCriterion implements RideSearchCriterion {

    private final String needle;

    public DestinationContainsCriterion(String destinationNeedle) {
        this.needle = Objects.requireNonNull(destinationNeedle, "destinationNeedle");
    }

    @Override
    public boolean matches(Ride ride) {
        String haystack = ride.getDestination();
        if (haystack == null) {
            return false;
        }
        return haystack.toLowerCase(Locale.ROOT).contains(needle.toLowerCase(Locale.ROOT));
    }
}
