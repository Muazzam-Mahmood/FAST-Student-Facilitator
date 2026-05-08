package com.fast.fsf.analytics.persistence;

import com.fast.fsf.analytics.domain.AnalyticsHit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository for managing AnalyticsHit entities.
 */
public interface AnalyticsHitRepository extends JpaRepository<AnalyticsHit, Long> {

    @Query("SELECT a.featureName as feature, COUNT(a) as count " +
           "FROM AnalyticsHit a " +
           "WHERE a.timestamp >= :since " +
           "GROUP BY a.featureName")
    List<Object[]> countHitsByFeatureSince(@Param("since") LocalDateTime since);
}
