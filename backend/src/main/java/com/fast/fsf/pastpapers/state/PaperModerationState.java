package com.fast.fsf.pastpapers.state;

import com.fast.fsf.pastpapers.domain.PastPaper;

/**
 * Interface for past paper moderation states.
 * Defines the moderation actions that can be performed on a paper.
 */
public interface PaperModerationState {
    void approve(PastPaper paper, String reason);
    void reject(PastPaper paper);
    void flag(PastPaper paper, String reason);
    void resolveFlag(PastPaper paper);
    String getStateName();
}
