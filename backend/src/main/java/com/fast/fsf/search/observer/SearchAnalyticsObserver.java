package com.fast.fsf.search.observer;

import com.fast.fsf.search.event.RideSearchPerformedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Observer that reacts to ride search events and logs search analytics.
 */
@Component
public class SearchAnalyticsObserver {

    private static final Logger log = LoggerFactory.getLogger(SearchAnalyticsObserver.class);

    @EventListener
    public void onRideSearchPerformed(RideSearchPerformedEvent event) {
        log.debug("[Search feature] criterion={} matches={}", event.getCriterionKindSummary(), event.getMatchCount());
    }
}
