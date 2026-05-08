package com.fast.fsf.pastpapers.criterion;

import com.fast.fsf.pastpapers.domain.PastPaper;

/**
 * Interface for past paper search criteria.
 * Each implementation encapsulates a specific filtering rule.
 */
public interface PaperSearchCriterion {
    boolean matches(PastPaper paper);
}
