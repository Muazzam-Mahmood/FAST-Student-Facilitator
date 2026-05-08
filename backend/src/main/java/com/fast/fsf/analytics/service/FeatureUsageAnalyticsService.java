package com.fast.fsf.analytics.service;

import com.fast.fsf.analytics.domain.AnalyticsHit;
import com.fast.fsf.analytics.persistence.AnalyticsHitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service for logging and retrieving feature usage analytics.
 * Includes debouncing logic to prevent duplicate hits.
 */
@Service
public class FeatureUsageAnalyticsService {

    @Autowired
    private AnalyticsHitRepository analyticsHitRepository;

    private final java.util.concurrent.ConcurrentHashMap<String, Long> lastHitMap = new java.util.concurrent.ConcurrentHashMap<>();

    public void logActivity(String featureName) {
        long now = System.currentTimeMillis();
        Long lastHit = lastHitMap.get(featureName);
        
        // Debounce: Ignore hits for the same feature within 500ms 
        // (prevents React StrictMode or double-click double counting)
        if (lastHit == null || (now - lastHit) > 500) {
            lastHitMap.put(featureName, now);
            analyticsHitRepository.save(new AnalyticsHit(featureName));
        }
    }

    public List<Map<String, Object>> getFeatureUsageStats(int days) {
        LocalDateTime since = LocalDateTime.now().minusDays(days);
        List<Object[]> results = analyticsHitRepository.countHitsByFeatureSince(since);

        return results.stream()
                .map(res -> Map.of(
                        "name", res[0],
                        "hits", res[1]
                ))
                .collect(Collectors.toList());
    }
}
