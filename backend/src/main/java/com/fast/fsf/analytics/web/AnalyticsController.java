package com.fast.fsf.analytics.web;

import com.fast.fsf.analytics.service.FeatureUsageAnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for analytics-related operations.
 * Provides statistics on feature usage for the admin panel.
 */
@RestController
@RequestMapping("/api/admin/analytics")
@CrossOrigin(origins = "http://localhost:5173")
public class AnalyticsController {

    @Autowired
    private FeatureUsageAnalyticsService analyticsService;

    @GetMapping("/feature-usage")
    public List<Map<String, Object>> getFeatureUsage() {
        return analyticsService.getFeatureUsageStats(30);
    }
}
