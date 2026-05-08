package com.fast.fsf.timetable.event;

import com.fast.fsf.timetable.domain.TimetableEntry;

/**
 * Event published when a timetable entry is deleted.
 */
public class TimetableDeletedEvent extends TimetableModeratedEvent {
    public TimetableDeletedEvent(Object source, TimetableEntry entry, String reason) { super(source, entry, reason); }
}
