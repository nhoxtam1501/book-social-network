package dev.ducku.bookapi.history;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BookTransactionHistoryRepository extends JpaRepository<BookTransactionHistory, Integer> {

    @Query("""
            SELECT h FROM BookTransactionHistory h
            WHERE h.userId = :userId
            """)
    Page<BookTransactionHistory> findAllBorrowedBooks(@Param("userId") String userId, Pageable pageable);


    @Query("""
            SELECT h FROM BookTransactionHistory h
            WHERE h.book.createdBy = :userId
            """)
    Page<BookTransactionHistory> findAllReturnedBooks(@Param("userId") String userId, Pageable pageable);

    @Query("""
            SELECT (COUNT(*) > 0) AS isBorrowed
            FROM BookTransactionHistory h
            WHERE h.userId = :userId
            AND h.book.id = :bookId
            AND h.returnApproved = false
            """)
    boolean isAlreadyBorrowedByUser(@Param("bookId") Integer bookId,@Param("userId") String userId);

    @Query("""
            SELECT (COUNT(*) > 0) AS isBorrowed
            FROM BookTransactionHistory h
            WHERE h.book.id = :bookId
            AND h.returnApproved = false
            """)
    boolean isAlreadyBorrowed(@Param("bookId") Integer bookId);

    @Query("""
              SELECT transaction
              FROM BookTransactionHistory transaction
              WHERE transaction.book.id = :bookId
              AND transaction.userId= :userId
              AND transaction.returnApproved = false
            """
    )
    Optional<BookTransactionHistory> findByBookIdAndUserId(@Param("bookId") Integer bookId, @Param("userId") String userId);

    @Query("""
                  SELECT transaction
                  FROM BookTransactionHistory transaction
                  WHERE transaction.book.id = :bookId
                  AND transaction.book.createdBy = :userId
                  AND transaction.returned = true
                  AND transaction.returnApproved = false
            """)
    Optional<BookTransactionHistory> findByBookIdAndOwnerId(@Param("bookId") Integer bookId, @Param("userId") String userId);

}
