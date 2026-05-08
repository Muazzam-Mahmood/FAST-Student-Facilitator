package com.fast.fsf.notes.service;

import com.fast.fsf.notes.domain.FastNote;
import com.fast.fsf.notes.domain.NoteVote;
import com.fast.fsf.notes.event.NoteDeletedEvent;
import com.fast.fsf.notes.event.NoteUploadedEvent;
import com.fast.fsf.notes.persistence.FastNoteRepository;
import com.fast.fsf.notes.persistence.NoteVoteRepository;
import com.fast.fsf.notes.template.StandardNoteQueryWorkflow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

/**
 * Service for managing FastNotes, including uploading, downloading, and voting.
 */
@Service
public class FastNoteService {

    @Autowired
    private FastNoteRepository repository;

    @Autowired
    private NoteVoteRepository voteRepository;

    @Autowired
    private StandardNoteQueryWorkflow queryWorkflow;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    // Queries

    /**
     * Returns active notes matching the supplied filters, with vote annotations populated.
     *
     * @param keyword      optional search keyword
     * @param subject      optional subject filter
     * @param studentEmail optional email for vote-type annotation
     * @return list of notes matching the criteria
     */
    public List<FastNote> getNotes(String keyword, String subject, String studentEmail) {
        return queryWorkflow.query(keyword, subject, studentEmail);
    }

    // -------------------------------------------------------------------------
    // File storage
    // -------------------------------------------------------------------------

    private final String UPLOAD_DIR = "uploads/notes/";

    /**
     * Validates, stores, and persists a new note file (PDF or DOCX).
     *
     * @param title        note title
     * @param subjectName  subject name
     * @param courseCode   course code
     * @param studentEmail uploader email
     * @param file         the multipart file to upload
     * @return the saved FastNote entity
     */
    public FastNote uploadNote(String title, String subjectName, String courseCode,
                               String studentEmail, MultipartFile file) {
        try {
            // Create directory if it doesn't exist
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String originalName = file.getOriginalFilename();
            if (originalName == null
                    || (!originalName.toLowerCase().endsWith(".pdf")
                        && !originalName.toLowerCase().endsWith(".docx"))) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Only PDF or DOCX files are accepted.");
            }

            String extension = originalName.substring(originalName.lastIndexOf("."));

            // Sanitize title but keep it recognizable
            String sanitizedTitle = title.replaceAll("[^a-zA-Z0-9.-]", "_");
            String fileName = UUID.randomUUID().toString() + "_" + sanitizedTitle + extension;
            Path filePath = uploadPath.resolve(fileName);

            // Save file
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Create Entity
            FastNote note = new FastNote(title, subjectName, courseCode, fileName,
                    studentEmail, LocalDate.now());
            FastNote saved = repository.save(note);

            // Observer: publish domain event
            eventPublisher.publishEvent(new NoteUploadedEvent(this, saved));

            return saved;
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to store file", e);
        }
    }

    public Path getNoteFile(String fileName) {
        return Paths.get(UPLOAD_DIR).resolve(fileName);
    }

    // Voting

    public FastNote voteNote(Long id, String studentEmail, String voteType) {
        FastNote note = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Note not found"));

        // Find existing vote for this user on this note
        var existingVote = voteRepository.findByNoteIdAndStudentEmail(id, studentEmail);

        if (existingVote.isPresent()) {
            NoteVote vote = existingVote.get();
            if (vote.getVoteType().equalsIgnoreCase(voteType)) {
                // If same vote type, user is clicking same button -> Remove vote (Toggle off)
                if ("UPVOTE".equalsIgnoreCase(voteType)) {
                    note.setUpvotes(Math.max(0, note.getUpvotes() - 1));
                } else {
                    note.setDownvotes(Math.max(0, note.getDownvotes() - 1));
                }
                voteRepository.delete(vote);
            } else {
                // If opposite vote type -> Switch vote
                if ("UPVOTE".equalsIgnoreCase(voteType)) {
                    note.setUpvotes(note.getUpvotes() + 1);
                    note.setDownvotes(Math.max(0, note.getDownvotes() - 1));
                } else {
                    note.setDownvotes(note.getDownvotes() + 1);
                    note.setUpvotes(Math.max(0, note.getUpvotes() - 1));
                }
                vote.setVoteType(voteType);
                voteRepository.save(vote);
            }
        } else {
            // New vote
            if ("UPVOTE".equalsIgnoreCase(voteType)) {
                note.setUpvotes(note.getUpvotes() + 1);
            } else {
                note.setDownvotes(note.getDownvotes() + 1);
            }
            voteRepository.save(new NoteVote(id, studentEmail, voteType));
        }

        return repository.save(note);
    }

    // Admin operations

    public FastNote getNoteByFileName(String fileName) {
        return repository.findByFileUrl(fileName).orElse(null);
    }

    /**
     * Soft-deletes a note (sets status to "Removed").
     *
     * @param id note to remove.
     */
    public void deleteNote(Long id) {
        FastNote note = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Note not found"));
        note.setStatus("Removed");
        repository.save(note);

        // Observer: publish domain event — audit logging decoupled from this service
        eventPublisher.publishEvent(new NoteDeletedEvent(this, id, "admin"));
    }

    public List<FastNote> getPendingNotes() {
        return repository.findByApprovedAndStatus(false, "Active");
    }

    public void approveNote(Long id) {
        FastNote note = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Note not found"));
        note.setApproved(true);
        repository.save(note);
    }
}
