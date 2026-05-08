package com.fast.fsf.notes.template;

import com.fast.fsf.notes.domain.FastNote;
import com.fast.fsf.notes.persistence.FastNoteRepository;
import com.fast.fsf.notes.persistence.NoteVoteRepository;

import java.util.List;

/**
 * Abstract workflow for querying notes.
 * Implements a template for fetching notes from the repository and enriching them with user votes.
 */
public abstract class AbstractNoteQueryWorkflow {

    protected final FastNoteRepository noteRepository;
    protected final NoteVoteRepository voteRepository;

    protected AbstractNoteQueryWorkflow(FastNoteRepository noteRepository,
                                        NoteVoteRepository voteRepository) {
        this.noteRepository = noteRepository;
        this.voteRepository = voteRepository;
    }

    /**
     * Executes the query workflow: fetch from repository and enrich with votes.
     *
     * @param keyword      optional search keyword
     * @param subject      optional subject filter
     * @param studentEmail optional email for vote-type annotation
     * @return enriched list of notes
     */
    public final List<FastNote> query(String keyword, String subject, String studentEmail) {
        // Step 1 — Hook: subclass selects the right repository query
        List<FastNote> notes = fetchFromRepo(keyword, subject, studentEmail);

        // Step 2 — Fixed: populate the transient userVoteType field if an email was provided
        if (studentEmail != null && !studentEmail.isEmpty()) {
            for (FastNote note : notes) {
                voteRepository.findByNoteIdAndStudentEmail(note.getId(), studentEmail)
                        .ifPresent(v -> note.setUserVoteType(v.getVoteType()));
            }
        }

        return notes;
    }

    /**
     * Hook — subclass returns the appropriate repository result set for the given filter
     * combination. Called by the template method; never call directly.
     *
     * @param keyword optional search keyword (may be {@code null}).
     * @param subject optional subject filter (may be {@code null}).
     * @return raw (un-enriched) list from the repository.
     */
    protected abstract List<FastNote> fetchFromRepo(String keyword, String subject, String studentEmail);
}
