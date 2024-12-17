package dev.ducku.bookapi.feedback;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FeedbackRepository extends JpaRepository<Feedback, Integer> {

    @Query("""
            select f from Feedback f
            WHERE f.book.id = :bookId
            """)
    Page<Feedback> findAllByBookId(@Param("bookId") Long bookId, Pageable pageable);
}
