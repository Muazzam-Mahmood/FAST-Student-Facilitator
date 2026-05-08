package com.fast.fsf.timetable.event;

import com.fast.fsf.timetable.domain.TimetableEntry;

/**
 * Event published when a flag on a timetable entry is resolved.
 */
public class TimetableResolvedEvent extends TimetableProposedEvent {
    public TimetableResolvedEvent(Object source, TimetableEntry entry) { super(source, entry); }
}
