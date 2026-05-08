package com.fast.fsf.pastpapers.template;

import com.fast.fsf.pastpapers.domain.PastPaper;
import com.fast.fsf.pastpapers.observer.PaperEventPublisher;
import com.fast.fsf.pastpapers.persistence.PastPaperRepository;

/**
 * Abstract workflow for past paper moderation actions.
 * Defines a template for validating, applying, and publishing moderation changes.
 */
public abstract class AbstractPaperModerationWorkflow {

    protected final PastPaperRepository paperRepository;
    protected final PaperEventPublisher eventPublisher;

    protected AbstractPaperModerationWorkflow(PastPaperRepository paperRepository, PaperEventPublisher eventPublisher) {
        this.paperRepository = paperRepository;
        this.eventPublisher = eventPublisher;
    }

    /**
     * Executes the moderation workflow for a specific paper.
     *
     * @param paperId the ID of the paper to moderate
     * @param reason  the reason for the moderation action
     * @return the updated PastPaper entity
     */
    public final PastPaper execute(Long paperId, String reason) {
        PastPaper paper = findPaper(paperId);
        validateTransition(paper);
        applyChange(paper, reason);
        PastPaper saved = paperRepository.save(paper);
        publishEvent(saved, reason);
        return saved;
    }

    protected PastPaper findPaper(Long id) {
        return paperRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Paper not found with ID: " + id));
    }

    protected abstract void validateTransition(PastPaper paper);

    protected abstract void applyChange(PastPaper paper, String reason);

    protected abstract void publishEvent(PastPaper paper, String reason);
}
