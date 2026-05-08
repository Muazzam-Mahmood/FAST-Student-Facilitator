package com.fast.fsf.search.template;

import com.fast.fsf.carpool.domain.Ride;
import com.fast.fsf.search.catalog.ApprovedRideSource;
import com.fast.fsf.search.criterion.RideSearchCriterion;

import java.util.List;

/**
 * Abstract executor for ride searches.
 * Implements the search algorithm by loading candidates and applying criteria.
 */
public abstract class AbstractRideSearchExecutor {

    private final ApprovedRideSource catalog;

    protected AbstractRideSearchExecutor(ApprovedRideSource catalog) {
        this.catalog = catalog;
    }

    /**
     * Executes the search workflow.
     *
     * @param criterion the criterion to apply
     * @return list of matching rides
     */
    public final List<Ride> search(RideSearchCriterion criterion) {
        List<Ride> pool = loadCandidatePool();
        return refine(pool, criterion);
    }

    /** Hook #1 — default: approved rides exposed through {@link ApprovedRideSource}. */
    protected List<Ride> loadCandidatePool() {
        return catalog.loadApprovedCandidates();
    }

    /** Hook #2 — default: linear filter via Composite / leaf criteria. */
    protected List<Ride> refine(List<Ride> pool, RideSearchCriterion criterion) {
        return pool.stream().filter(criterion::matches).toList();
    }
}
