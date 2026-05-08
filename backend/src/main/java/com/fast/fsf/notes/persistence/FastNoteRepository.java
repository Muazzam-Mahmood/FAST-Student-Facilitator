package com.fast.fsf.notes.persistence;

import com.fast.fsf.notes.domain.FastNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for managing FastNote entities.
 */
@Repository
public interface FastNoteRepository extends JpaRepository<FastNote, Long> {
    Optional<FastNote> findByFileUrl(String fileUrl);
    
    List<FastNote> findByApprovedAndStatus(boolean approved, String status);

    @Query("SELECT n FROM FastNote n WHERE n.status = 'Active' AND (n.approved = true OR n.studentEmail = :studentEmail) ORDER BY (n.upvotes - n.downvotes) DESC, n.uploadDate DESC")
    List<FastNote> findAllActiveOrdered(@Param("studentEmail") String studentEmail);

    @Query("SELECT n FROM FastNote n WHERE n.status = 'Active' AND (n.approved = true OR n.studentEmail = :studentEmail) AND (LOWER(n.subjectName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(n.courseCode) LIKE LOWER(CONCAT('%', :keyword, '%'))) ORDER BY (n.upvotes - n.downvotes) DESC, n.uploadDate DESC")
    List<FastNote> searchByKeywordOrdered(@Param("keyword") String keyword, @Param("studentEmail") String studentEmail);

    @Query("SELECT n FROM FastNote n WHERE n.status = 'Active' AND (n.approved = true OR n.studentEmail = :studentEmail) AND LOWER(n.subjectName) = LOWER(:subject) ORDER BY (n.upvotes - n.downvotes) DESC, n.uploadDate DESC")
    List<FastNote> filterBySubjectOrdered(@Param("subject") String subject, @Param("studentEmail") String studentEmail);
    
    @Query("SELECT n FROM FastNote n WHERE n.status = 'Active' AND (n.approved = true OR n.studentEmail = :studentEmail) AND LOWER(n.subjectName) = LOWER(:subject) AND (LOWER(n.subjectName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(n.courseCode) LIKE LOWER(CONCAT('%', :keyword, '%'))) ORDER BY (n.upvotes - n.downvotes) DESC, n.uploadDate DESC")
    List<FastNote> searchAndFilterOrdered(@Param("keyword") String keyword, @Param("subject") String subject, @Param("studentEmail") String studentEmail);
}
