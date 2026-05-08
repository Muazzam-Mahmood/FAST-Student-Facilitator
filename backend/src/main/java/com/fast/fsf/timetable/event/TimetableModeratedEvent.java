package com.fast.fsf.timetable.event;

import com.fast.fsf.timetable.domain.TimetableEntry;

/**
 * Event published when a timetable entry undergoes moderation (approval or flagging).
 */
public class TimetableModeratedEvent extends TimetableProposedEvent {
    private final String moderationReason;

    public TimetableModeratedEvent(Object source, TimetableEntry entry, String moderationReason) {
        super(source, entry);
        this.moderationReason = moderationReason;
    }

    public String getModerationReason() { return moderationReason; }
}
