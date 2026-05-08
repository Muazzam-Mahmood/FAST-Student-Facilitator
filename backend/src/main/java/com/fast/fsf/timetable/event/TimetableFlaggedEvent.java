package com.fast.fsf.timetable.event;

import com.fast.fsf.timetable.domain.TimetableEntry;

/**
 * Event published when a timetable entry is flagged.
 */
public class TimetableFlaggedEvent extends TimetableModeratedEvent {
    public TimetableFlaggedEvent(Object source, TimetableEntry entry, String reason) { super(source, entry, reason); }
}
