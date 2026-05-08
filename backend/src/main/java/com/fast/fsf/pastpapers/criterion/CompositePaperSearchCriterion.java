package com.fast.fsf.pastpapers.criterion;

import com.fast.fsf.pastpapers.domain.PastPaper;
import java.util.List;

/**
 * A composite implementation of PaperSearchCriterion that combines multiple criteria using logical AND.
 */
public class CompositePaperSearchCriterion implements PaperSearchCriterion {
    private final List<PaperSearchCriterion> criteria;

    public CompositePaperSearchCriterion(List<PaperSearchCriterion> criteria) {
        this.criteria = criteria;
    }

    @Override
    public boolean matches(PastPaper paper) {
        return criteria.stream().allMatch(c -> c.matches(paper));
    }
}
