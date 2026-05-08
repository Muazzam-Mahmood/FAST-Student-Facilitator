package com.fast.fsf.search.event;

import org.springframework.context.ApplicationEvent;

/**
 * Event published when a ride search is performed.
 */
public class RideSearchPerformedEvent extends ApplicationEvent {

    private final String criterionKindSummary;
    private final int matchCount;

    public RideSearchPerformedEvent(Object source, String criterionKindSummary, int matchCount) {
        super(source);
        this.criterionKindSummary = criterionKindSummary;
        this.matchCount = matchCount;
    }

    public String getCriterionKindSummary() {
        return criterionKindSummary;
    }

    public int getMatchCount() {
        return matchCount;
    }
}
