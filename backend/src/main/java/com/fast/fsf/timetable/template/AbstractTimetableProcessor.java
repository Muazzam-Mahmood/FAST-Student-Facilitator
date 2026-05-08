package com.fast.fsf.timetable.template;

import com.fast.fsf.timetable.domain.TimetableEntry;
import com.fast.fsf.timetable.event.TimetableUploadedEvent;
import com.fast.fsf.timetable.persistence.TimetableEntryRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;

import java.io.InputStream;
import java.util.List;

/**
 * Abstract processor for timetable file uploads.
 * Defines the workflow for parsing, saving, and publishing upload events.
 */
public abstract class AbstractTimetableProcessor {

    protected final TimetableEntryRepository timetableRepository;
    protected final ApplicationEventPublisher eventPublisher;

    public AbstractTimetableProcessor(TimetableEntryRepository timetableRepository, ApplicationEventPublisher eventPublisher) {
        this.timetableRepository = timetableRepository;
        this.eventPublisher = eventPublisher;
    }

    /**
     * Processes the timetable file: parse entries, update database, and publish events.
     *
     * @param is         input stream of the file
     * @param ownerName  name of the uploader
     * @param ownerEmail email of the uploader
     * @return response entity with the result of the operation
     */
    public final ResponseEntity<?> process(InputStream is, String ownerName, String ownerEmail) {
        try {
            List<TimetableEntry> entries = parseEntries(is, ownerName, ownerEmail);

            if (entries.isEmpty()) {
                return ResponseEntity.badRequest().body("No valid classes found in the file.");
            }

            // Fast batch delete then batch insert (avoids N individual DELETE statements)
            timetableRepository.deleteAllInBatch(timetableRepository.findByApprovedTrue());
            timetableRepository.saveAllAndFlush(entries);

            eventPublisher.publishEvent(new TimetableUploadedEvent(this, ownerName, entries.size()));

            // Return summary only – not the full list (avoids serialising thousands of rows)
            return ResponseEntity.ok(java.util.Map.of(
                "message", "Timetable uploaded successfully.",
                "count", entries.size()
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error parsing file: " + e.getMessage());
        }
    }

    /**
     * Hook method to be implemented by subclasses.
     */
    protected abstract List<TimetableEntry> parseEntries(InputStream is, String ownerName, String ownerEmail) throws Exception;
}
