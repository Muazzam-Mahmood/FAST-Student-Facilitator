package com.fast.fsf.pastpapers.criterion;

import com.fast.fsf.pastpapers.domain.PastPaper;

/**
 * Criterion for filtering past papers that are approved.
 */
public class ApprovedOnlyCriterion implements PaperSearchCriterion {
    @Override
    public boolean matches(PastPaper paper) {
        return paper.isApproved();
    }
}
