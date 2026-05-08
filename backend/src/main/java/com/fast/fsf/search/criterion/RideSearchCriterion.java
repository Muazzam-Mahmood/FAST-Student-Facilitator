package com.fast.fsf.search.criterion;

import com.fast.fsf.carpool.domain.Ride;

/**
 * Interface for ride search criteria.
 * Implementations define logic to match specific ride attributes.
 */
@FunctionalInterface
public interface RideSearchCriterion {

    boolean matches(Ride ride);
}
