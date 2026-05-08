package com.fast.fsf.campusmap.domain;

import jakarta.persistence.*;

/**
 * Entity representing a single step in a campus map route.
 */
@Entity
@Table(name = "campus_map_routes")
public class CampusMapRoute {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** blockId / location key, e.g. "BLOCK_C" */
    @Column(nullable = false)
    private String fromLocation;

    /** blockId / location key, e.g. "BLOCK_F" */
    @Column(nullable = false)
    private String toLocation;

    /** 1-based ordering of steps within a route */
    @Column(nullable = false)
    private int stepOrder;

    /**
     * Filename served at /api/campus-map/images/{filename}.
     * null = text-only step (no image uploaded yet).
     */
    @Column(nullable = true)
    private String imageFileName;

    /** Caption shown beneath the step image or inside the placeholder box */
    @Column(nullable = false)
    private String stepDescription;

    @Column(nullable = false)
    private String ownerEmail;

    @Column(nullable = false)
    private String ownerName;

    // ── Moderation triplet ────────────────────────────────────────────────────
    @Column(nullable = false)
    private boolean approved = true;

    @Column(nullable = false)
    private boolean flagged = false;

    private String moderationReason;

    // ── Default no-arg constructor ────────────────────────────────────────────
    public CampusMapRoute() {
        this.approved = true;
        this.flagged = false;
    }

    // ── Getters & Setters ─────────────────────────────────────────────────────

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFromLocation() { return fromLocation; }
    public void setFromLocation(String fromLocation) { this.fromLocation = fromLocation; }

    public String getToLocation() { return toLocation; }
    public void setToLocation(String toLocation) { this.toLocation = toLocation; }

    public int getStepOrder() { return stepOrder; }

    /**
     * Business rule: stepOrder must be >= 1.
     * Throw if violated so the controller can convert to a 400.
     */
    public void setStepOrder(int stepOrder) {
        if (stepOrder < 1) {
            throw new IllegalArgumentException("Step order must be 1 or greater");
        }
        this.stepOrder = stepOrder;
    }

    public String getImageFileName() { return imageFileName; }
    public void setImageFileName(String imageFileName) { this.imageFileName = imageFileName; }

    public String getStepDescription() { return stepDescription; }
    public void setStepDescription(String stepDescription) { this.stepDescription = stepDescription; }

    public String getOwnerEmail() { return ownerEmail; }
    public void setOwnerEmail(String ownerEmail) { this.ownerEmail = ownerEmail; }

    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }

    public boolean isApproved() { return approved; }
    public void setApproved(boolean approved) { this.approved = approved; }

    public boolean isFlagged() { return flagged; }
    public void setFlagged(boolean flagged) { this.flagged = flagged; }

    public String getModerationReason() { return moderationReason; }
    public void setModerationReason(String moderationReason) { this.moderationReason = moderationReason; }
}
