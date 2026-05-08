package com.fast.fsf.campusmap.criterion;

import com.fast.fsf.campusmap.domain.CampusLocation;

/**
 * Interface for campus location search criteria.
 * Each implementation encapsulates a specific filtering rule.
 */
public interface LocationSearchCriterion {
    boolean matches(CampusLocation location);
}
