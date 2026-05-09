package com.fast.fsf.pastpapers.observer;

import com.fast.fsf.pastpapers.domain.PaperComment;
import com.fast.fsf.pastpapers.domain.PastPaper;
import com.fast.fsf.shared.domain.ActivityLog;
import com.fast.fsf.shared.persistence.ActivityLogRepository;
import org.springframework.stereotype.Component;

@Component
public class PaperActivityLogObserver implements PaperEventListener {

    private final ActivityLogRepository activityLogRepo;

    public PaperActivityLogObserver(ActivityLogRepository activityLogRepo) {
        this.activityLogRepo = activityLogRepo;
    }

    @Override
    public void onPaperUploaded(PastPaper paper) {
        activityLogRepo.save(new ActivityLog("PAPER_UPLOADED", 
            "Paper uploaded: " + paper.getCourseName(), paper.getOwnerEmail()));
    }

    @Override
    public void onPaperApproved(PastPaper paper) {
        activityLogRepo.save(new ActivityLog("PAPER_APPROVED", 
            "Paper approved: " + paper.getCourseName(), "admin@nu.edu.pk"));
    }

    @Override
    public void onPaperFlagged(PastPaper paper, String reason) {
        activityLogRepo.save(new ActivityLog("PAPER_FLAGGED", 
            "Paper flagged: " + paper.getCourseName() + " Reason: " + reason, "admin@nu.edu.pk"));
    }

    @Override
    public void onPaperFlagResolved(PastPaper paper) {
        activityLogRepo.save(new ActivityLog("PAPER_RESOLVED", 
            "Paper flag resolved: " + paper.getCourseName(), "admin@nu.edu.pk"));
    }

    @Override
    public void onPaperDeleted(PastPaper paper, String reason) {
        activityLogRepo.save(new ActivityLog("PAPER_DELETED", 
            "Paper deleted: " + paper.getCourseName() + " Reason: " + reason, "admin@nu.edu.pk"));
    }

    @Override
    public void onPaperRejected(PastPaper paper) {
        activityLogRepo.save(new ActivityLog("PAPER_REJECTED", 
            "Paper rejected: " + paper.getCourseName(), "admin@nu.edu.pk"));
    }

    @Override
    public void onPaperRated(PastPaper paper, String studentEmail, int rating) {
        activityLogRepo.save(new ActivityLog("PAPER_RATED", 
            "Paper rated: " + paper.getCourseName() + " Rating: " + rating, studentEmail));
    }

    @Override
    public void onPaperReported(PastPaper paper, String reporterEmail, String reason) {
        activityLogRepo.save(new ActivityLog("PAPER_REPORTED", 
            "Paper reported: " + paper.getCourseName() + " Reason: " + reason, reporterEmail));
    }

    @Override
    public void onCommentPosted(PaperComment comment) {
        activityLogRepo.save(new ActivityLog("COMMENT_POSTED", 
            "Comment posted on paper ID: " + comment.getPaperId(), comment.getStudentEmail()));
    }

    @Override
    public void onCommentDeleted(PaperComment comment, String studentEmail) {
        activityLogRepo.save(new ActivityLog("COMMENT_DELETED", 
            "Comment deleted on paper ID: " + comment.getPaperId(), studentEmail));
    }

    @Override
    public void onReportResolved(Long reportId) {
        activityLogRepo.save(new ActivityLog("REPORT_RESOLVED", 
            "Report resolved ID: " + reportId, "admin@nu.edu.pk"));
    }

    @Override
    public void onPaperDownloaded(PastPaper paper) {
        activityLogRepo.save(new ActivityLog("PAPER_DOWNLOADED", 
            "Paper downloaded: " + paper.getCourseName(), "student@nu.edu.pk"));
    }
}
