package com.fast.fsf.timetable.event;

import com.fast.fsf.timetable.domain.TimetableEntry;

/**
 * Event published when a timetable entry is approved.
 */
public class TimetableApprovedEvent extends TimetableModeratedEvent {
    public TimetableApprovedEvent(Object source, TimetableEntry entry, String reason) { super(source, entry, reason); }
}
