package com.fast.fsf.timetable.event;

import com.fast.fsf.timetable.domain.TimetableEntry;

/**
 * Event published when a new timetable entry is proposed.
 */
public class TimetableProposedEvent extends TimetableEvent {
    private final TimetableEntry entry;

    public TimetableProposedEvent(Object source, TimetableEntry entry) {
        super(source);
        this.entry = entry;
    }

    public TimetableEntry getEntry() { return entry; }
}
