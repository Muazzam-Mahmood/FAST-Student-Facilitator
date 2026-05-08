package com.fast.fsf.campusmap.criterion;

import com.fast.fsf.campusmap.domain.CampusLocation;

/**
 * Criterion for filtering campus locations that are approved.
 */
public class ApprovedLocationCriterion implements LocationSearchCriterion {
    @Override
    public boolean matches(CampusLocation location) {
        return location.isApproved();
    }
}
