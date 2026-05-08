package com.fast.fsf.search.service;

import com.fast.fsf.carpool.domain.Ride;
import com.fast.fsf.search.catalog.ApprovedRideSource;
import com.fast.fsf.search.criterion.RideSearchCriterion;
import com.fast.fsf.search.event.RideSearchPerformedEvent;
import com.fast.fsf.search.template.AbstractRideSearchExecutor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service for searching rides using various criteria.
 */
@Service
public class RideSearchService extends AbstractRideSearchExecutor {

    private final ApplicationEventPublisher eventPublisher;

    public RideSearchService(ApprovedRideSource catalog, ApplicationEventPublisher eventPublisher) {
        super(catalog);
        this.eventPublisher = eventPublisher;
    }

    /**
     * Searches for approved rides based on the given criterion.
     *
     * @param criterion the search criterion to apply
     * @return list of matching rides
     */
    public List<Ride> searchApproved(RideSearchCriterion criterion) {
        List<Ride> matches = search(criterion);
        eventPublisher.publishEvent(new RideSearchPerformedEvent(
                this,
                criterion.getClass().getSimpleName(),
                matches.size()));
        return matches;
    }
}
