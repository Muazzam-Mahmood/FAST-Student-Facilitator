package com.fast.fsf.events.persistence;

import com.fast.fsf.events.domain.CampusEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository for managing CampusEvent entities.
 */
@Repository
public interface CampusEventRepository extends JpaRepository<CampusEvent, Long> {
    
    // View Event Board
    List<CampusEvent> findByApprovedTrueOrderByEventDateAsc();
    
    List<CampusEvent> findByApprovedTrueAndSemesterPlanFalseOrderByEventDateAsc();

    // Event Board feed: every approved event whose organizer is NOT one of the
    // calendar-import organizers. A user-posted event with semesterPlan=true
    // therefore shows in BOTH the Event Board and the Semester Plan.
    List<CampusEvent> findByApprovedTrueAndOrganizerNotInOrderByEventDateAsc(List<String> organizers);
    
    // Semester Plan
    List<CampusEvent> findByApprovedTrueAndSemesterPlanTrueOrderByEventDateAsc();
    
    List<CampusEvent> findByApprovedFalse();
    
    List<CampusEvent> findByOwnerEmail(String email);
    
    List<CampusEvent> findByEventDateAfterAndApprovedTrue(LocalDate date);

    // Calendar-imported semester-plan items (organizer set by the parser).
    // Used to wipe only previously-imported items on re-upload, leaving
    // user-posted semester-plan items untouched.
    List<CampusEvent> findBySemesterPlanTrueAndOrganizerIn(List<String> organizers);

    // Campus Map integration
    List<CampusEvent> findByVenueContainingIgnoreCaseAndApprovedTrue(String venue);
}
