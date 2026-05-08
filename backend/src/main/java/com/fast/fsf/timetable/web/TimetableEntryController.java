package com.fast.fsf.timetable.web;

import com.fast.fsf.timetable.domain.TimetableEntry;
import com.fast.fsf.timetable.event.*;
import com.fast.fsf.timetable.persistence.TimetableEntryRepository;
import com.fast.fsf.timetable.template.CsvTimetableProcessor;
import com.fast.fsf.timetable.template.ExcelTimetableProcessor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.InputStream;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

/**
 * REST Controller for timetable-related operations.
 * Handles HTTP requests for listing, uploading, and managing timetable entries.
 */
@RestController
@RequestMapping("/api/timetable")
@CrossOrigin(origins = "http://localhost:5173")
public class TimetableEntryController {

    private final TimetableEntryRepository timetableRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final ExcelTimetableProcessor excelProcessor;
    private final CsvTimetableProcessor csvProcessor;
    private final RestTemplate restTemplate = new RestTemplate();

    public TimetableEntryController(TimetableEntryRepository timetableRepository, ApplicationEventPublisher eventPublisher, ExcelTimetableProcessor excelProcessor, CsvTimetableProcessor csvProcessor) {
        this.timetableRepository = timetableRepository;
        this.eventPublisher = eventPublisher;
        this.excelProcessor = excelProcessor;
        this.csvProcessor = csvProcessor;
    }

    @GetMapping
    public List<TimetableEntry> getAllEntries() {
        return timetableRepository.findByApprovedTrue();
    }

    @GetMapping("/section")
    public List<TimetableEntry> getSectionTimetable(
            @RequestParam String department,
            @RequestParam String batch,
            @RequestParam String section) {
        String d = department.trim();
        String b = batch.trim();
        String s = section.trim();
        System.out.println("DEBUG: Querying timetable for Dept: [" + d + "], Batch: [" + b + "], Section: [" + s + "]");
        return timetableRepository.findByDepartmentAndBatchAndSectionAndApprovedTrue(d, b, s);
    }

    @PostMapping
    public ResponseEntity<TimetableEntry> createEntry(@RequestBody TimetableEntry entry) {
        entry.setApproved(false);
        TimetableEntry saved = timetableRepository.save(entry);
        eventPublisher.publishEvent(new TimetableProposedEvent(this, saved));
        return ResponseEntity.ok(saved);
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadTimetable(
            @RequestParam String url,
            @RequestParam String ownerName,
            @RequestParam String ownerEmail) {
        try {
            String csvData = restTemplate.getForObject(url, String.class);
            if (csvData == null) return ResponseEntity.badRequest().body("Failed to fetch data from URL.");
            InputStream is = new java.io.ByteArrayInputStream(csvData.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            return csvProcessor.process(is, ownerName, ownerEmail);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/upload-file")
    public ResponseEntity<?> uploadTimetableFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam String ownerName,
            @RequestParam String ownerEmail) {
        try {
            String fileName = file.getOriginalFilename();
            if (fileName != null && (fileName.endsWith(".xlsx") || fileName.endsWith(".xls"))) {
                return excelProcessor.process(file.getInputStream(), ownerName, ownerEmail);
            }
            return csvProcessor.process(file.getInputStream(), ownerName, ownerEmail);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error parsing file: " + e.getMessage());
        }
    }

    @GetMapping("/pending")
    public List<TimetableEntry> getPendingEntries() {
        return timetableRepository.findByApprovedFalse();
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<TimetableEntry> approveEntry(@PathVariable Long id, @RequestParam(required = false) String reason) {
        return timetableRepository.findById(id).map(entry -> {
            entry.setApproved(true);
            entry.setModerationReason(reason);
            TimetableEntry saved = timetableRepository.save(entry);
            eventPublisher.publishEvent(new TimetableApprovedEvent(this, saved, reason));
            return ResponseEntity.ok(saved);
        }).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/flagged/count")
    public long getFlaggedCount() {
        return timetableRepository.countByFlaggedTrue();
    }

    @GetMapping("/flagged")
    public List<TimetableEntry> getFlaggedEntries() {
        return timetableRepository.findByFlaggedTrue();
    }

    @PutMapping("/{id}/flag")
    public ResponseEntity<TimetableEntry> flagEntry(@PathVariable Long id, @RequestParam(required = false) String reason) {
        return timetableRepository.findById(id).map(entry -> {
            entry.setFlagged(true);
            entry.setModerationReason(reason);
            TimetableEntry saved = timetableRepository.save(entry);
            eventPublisher.publishEvent(new TimetableFlaggedEvent(this, saved, reason));
            return ResponseEntity.ok(saved);
        }).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/resolve")
    public ResponseEntity<TimetableEntry> resolveEntry(@PathVariable Long id) {
        return timetableRepository.findById(id).map(entry -> {
            entry.setFlagged(false);
            entry.setModerationReason(null);
            TimetableEntry saved = timetableRepository.save(entry);
            eventPublisher.publishEvent(new TimetableResolvedEvent(this, saved));
            return ResponseEntity.ok(saved);
        }).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/count/active")
    public long getActiveCount() {
        return timetableRepository.countByApprovedTrue();
    }

    @GetMapping("/search")
    public List<TimetableEntry> searchEntries(@RequestParam String query) {
        return timetableRepository.findByCourseNameContainingIgnoreCaseAndApprovedTrue(query);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEntry(@PathVariable Long id, @RequestParam(required = false) String reason) {
        return timetableRepository.findById(id).map(entry -> {
            eventPublisher.publishEvent(new TimetableDeletedEvent(this, entry, reason));
            timetableRepository.deleteById(id);
            return ResponseEntity.ok().<Void>build();
        }).orElse(ResponseEntity.notFound().build());
    }
}
