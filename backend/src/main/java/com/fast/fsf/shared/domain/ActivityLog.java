package com.fast.fsf.shared.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "activity_log")
public class ActivityLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String actionType; // e.g., "PAPER_UPLOADED", "LOCATION_ADDED"

    @Column(nullable = false)
    private String details;

    @Column(nullable = false)
    private String userEmail;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    public ActivityLog() {
    }

    public ActivityLog(String actionType, String details, String userEmail) {
        this.actionType = actionType;
        this.details = details;
        this.userEmail = userEmail;
        this.timestamp = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getActionType() { return actionType; }
    public void setActionType(String actionType) { this.actionType = actionType; }

    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
