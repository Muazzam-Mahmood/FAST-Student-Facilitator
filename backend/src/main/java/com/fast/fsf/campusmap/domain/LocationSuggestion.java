package com.fast.fsf.campusmap.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity representing a student's suggestion for a new campus location.
 */
@Entity
@Table(name = "location_suggestions")
public class LocationSuggestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String locationName;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false, length = 1000)
    private String description;

    /** Student's university email */
    @Column(nullable = false)
    private String submittedBy;

    @Column(nullable = false)
    private String submitterName;

    private LocalDateTime submittedAt;

    /** false = pending admin review; true = admin has reviewed it */
    @Column(nullable = false)
    private boolean resolved = false;

    // ── Moderation triplet ────────────────────────────────────────────────────
    @Column(nullable = false)
    private boolean approved = false;

    @Column(nullable = false)
    private boolean flagged = false;

    private String moderationReason;

    // ── Default no-arg constructor ────────────────────────────────────────────
    public LocationSuggestion() {
        this.resolved = false;
        this.approved = false;
        this.flagged = false;
    }

    // ── Getters & Setters ─────────────────────────────────────────────────────

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getLocationName() { return locationName; }
    public void setLocationName(String locationName) { this.locationName = locationName; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getSubmittedBy() { return submittedBy; }
    public void setSubmittedBy(String submittedBy) { this.submittedBy = submittedBy; }

    public String getSubmitterName() { return submitterName; }
    public void setSubmitterName(String submitterName) { this.submitterName = submitterName; }

    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }

    public boolean isResolved() { return resolved; }
    public void setResolved(boolean resolved) { this.resolved = resolved; }

    public boolean isApproved() { return approved; }
    public void setApproved(boolean approved) { this.approved = approved; }

    public boolean isFlagged() { return flagged; }
    public void setFlagged(boolean flagged) { this.flagged = flagged; }

    public String getModerationReason() { return moderationReason; }
    public void setModerationReason(String moderationReason) { this.moderationReason = moderationReason; }
}
