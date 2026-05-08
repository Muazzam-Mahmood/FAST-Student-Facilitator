package com.fast.fsf.search.catalog;

import com.fast.fsf.carpool.domain.Ride;

import java.util.List;

/**
 * Interface for providing a source of approved rides for searching.
 */
public interface ApprovedRideSource {

    /** Candidates eligible for UC‑05 matching (today: approved listings only). */
    List<Ride> loadApprovedCandidates();
}
