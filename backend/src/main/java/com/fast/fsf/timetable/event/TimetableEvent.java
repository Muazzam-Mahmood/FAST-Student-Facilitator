package com.fast.fsf.timetable.event;

import org.springframework.context.ApplicationEvent;

/**
 * Base class for all timetable-related events.
 */
public class TimetableEvent extends ApplicationEvent {
    public TimetableEvent(Object source) {
        super(source);
    }
}
