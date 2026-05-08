package com.fast.fsf.admin.web;

import com.fast.fsf.carpool.persistence.RideRepository;
import org.springframework.web.bind.annotation.*;

/**
 * AdminController
 * 
 * Provides centralized statistics and logging for the Portal Admin.
 */
@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:5173")
public class AdminController {

    private final RideRepository rideRepository;
    
    public AdminController(RideRepository rideRepository) {
        this.rideRepository = rideRepository;
    }

    /**
     * GET /api/admin/pending/count
     * Returns the number of items waiting for approval.
     */
    @GetMapping("/pending/count")
    public long getPendingCount() {
        return rideRepository.countByApprovedFalse();
    }
}
