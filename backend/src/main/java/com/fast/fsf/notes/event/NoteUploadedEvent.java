package com.fast.fsf.notes.event;

import com.fast.fsf.notes.domain.FastNote;
import org.springframework.context.ApplicationEvent;

/**
 * Event published when a new study note is uploaded.
 */
public class NoteUploadedEvent extends ApplicationEvent {

    private final FastNote savedNote;

    public NoteUploadedEvent(Object source, FastNote savedNote) {
        super(source);
        this.savedNote = savedNote;
    }

    public FastNote getSavedNote() {
        return savedNote;
    }
}
