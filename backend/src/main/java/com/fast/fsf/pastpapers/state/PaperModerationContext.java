package com.fast.fsf.pastpapers.state;

import com.fast.fsf.pastpapers.domain.PastPaper;

/**
 * Context for managing past paper moderation states.
 * Routes moderation actions to the appropriate state implementation.
 */
public class PaperModerationContext {

    private final PaperModerationState state;

    public PaperModerationContext(PastPaper paper) {
        if (paper.isFlagged()) {
            this.state = new FlaggedPaperState();
        } else if (paper.isApproved()) {
            this.state = new ApprovedPaperState();
        } else {
            this.state = new PendingPaperState();
        }
    }

    public void approve(PastPaper paper, String reason) {
        state.approve(paper, reason);
    }

    public void reject(PastPaper paper) {
        state.reject(paper);
    }

    public void flag(PastPaper paper, String reason) {
        state.flag(paper, reason);
    }

    public void resolveFlag(PastPaper paper) {
        state.resolveFlag(paper);
    }

    public String getStateName() {
        return state.getStateName();
    }
}
