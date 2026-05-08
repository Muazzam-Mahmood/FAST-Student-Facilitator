package com.fast.fsf.search.factory;

import com.fast.fsf.search.criterion.CompositeRideSearchCriterion;
import com.fast.fsf.search.criterion.DestinationContainsCriterion;
import com.fast.fsf.search.criterion.RideSearchCriterion;
import org.springframework.stereotype.Component;

/**
 * Factory for creating ride search criteria.
 */
@Component
public class RideSearchCriterionFactory {

    /**
     * Creates a criterion for searching approved rides by destination.
     *
     * @param destinationQuery the destination to search for
     * @return the search criterion
     */
    public RideSearchCriterion approvedListingDestinationContains(String destinationQuery) {
        return new CompositeRideSearchCriterion(new DestinationContainsCriterion(destinationQuery));
    }
}
