package com.fast.fsf.analytics.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity representing a single hit (usage) of a feature for analytics.
 */
@Entity
@Table(name = "analytics_hits")
public class AnalyticsHit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String featureName;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    public AnalyticsHit() {}

    public AnalyticsHit(String featureName) {
        this.featureName = featureName;
        this.timestamp = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public String getFeatureName() { return featureName; }
    public LocalDateTime getTimestamp() { return timestamp; }
}
