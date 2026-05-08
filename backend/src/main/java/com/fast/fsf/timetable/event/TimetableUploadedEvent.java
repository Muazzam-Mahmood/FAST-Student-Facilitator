package com.fast.fsf.timetable.event;

/**
 * Event published when a timetable file is successfully uploaded and processed.
 */
public class TimetableUploadedEvent extends TimetableEvent {
    private final String ownerName;
    private final int entryCount;

    public TimetableUploadedEvent(Object source, String ownerName, int entryCount) {
        super(source);
        this.ownerName = ownerName;
        this.entryCount = entryCount;
    }

    public String getOwnerName() { return ownerName; }
    public int getEntryCount() { return entryCount; }
}
