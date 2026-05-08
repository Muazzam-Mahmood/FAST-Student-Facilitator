package com.fast.fsf.pastpapers.template;

import com.fast.fsf.pastpapers.domain.PastPaper;
import com.fast.fsf.pastpapers.observer.PaperEventPublisher;
import com.fast.fsf.pastpapers.persistence.PastPaperRepository;
import com.fast.fsf.pastpapers.state.PaperModerationContext;
import org.springframework.stereotype.Service;

/**
 * Concrete Workflow for approving a paper.
 */
@Service
public class ApprovePaperWorkflow extends AbstractPaperModerationWorkflow {

    public ApprovePaperWorkflow(PastPaperRepository paperRepository, PaperEventPublisher eventPublisher) {
        super(paperRepository, eventPublisher);
    }

    @Override
    protected void validateTransition(PastPaper paper) {
        if (paper.isApproved()) {
            throw new IllegalStateException("Paper is already approved");
        }
    }

    @Override
    protected void applyChange(PastPaper paper, String reason) {
        new PaperModerationContext(paper).approve(paper, reason);
    }

    @Override
    protected void publishEvent(PastPaper paper, String reason) {
        eventPublisher.publishPaperApproved(paper);
    }
}
