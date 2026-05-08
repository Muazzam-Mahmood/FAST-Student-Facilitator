package com.fast.fsf.notes.event;

import org.springframework.context.ApplicationEvent;

/**
 * Event published when a study note is deleted.
 */
public class NoteDeletedEvent extends ApplicationEvent {

    private final Long noteId;
    private final String deletedByEmail;

    public NoteDeletedEvent(Object source, Long noteId, String deletedByEmail) {
        super(source);
        this.noteId = noteId;
        this.deletedByEmail = deletedByEmail;
    }

    public Long getNoteId() {
        return noteId;
    }

    public String getDeletedByEmail() {
        return deletedByEmail;
    }
}
