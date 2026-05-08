package com.fast.fsf.notes.template;

import com.fast.fsf.notes.domain.FastNote;
import com.fast.fsf.notes.persistence.FastNoteRepository;
import com.fast.fsf.notes.persistence.NoteVoteRepository;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Standard implementation of the note query workflow.
 * Handles the specific filtering logic for searching and filtering notes.
 */
@Component
public class StandardNoteQueryWorkflow extends AbstractNoteQueryWorkflow {

    public StandardNoteQueryWorkflow(FastNoteRepository noteRepository,
                                     NoteVoteRepository voteRepository) {
        super(noteRepository, voteRepository);
    }

    /**
     * Fetches notes from the repository based on the provided filters.
     */
    @Override
    protected List<FastNote> fetchFromRepo(String keyword, String subject, String studentEmail) {
        if (keyword != null && !keyword.isEmpty() && subject != null && !subject.isEmpty()) {
            return noteRepository.searchAndFilterOrdered(keyword, subject, studentEmail);
        } else if (keyword != null && !keyword.isEmpty()) {
            return noteRepository.searchByKeywordOrdered(keyword, studentEmail);
        } else if (subject != null && !subject.isEmpty()) {
            return noteRepository.filterBySubjectOrdered(subject, studentEmail);
        } else {
            return noteRepository.findAllActiveOrdered(studentEmail);
        }
    }
}
