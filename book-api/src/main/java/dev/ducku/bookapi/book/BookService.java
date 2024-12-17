package dev.ducku.bookapi.book;

import dev.ducku.bookapi.common.PageResponse;
import dev.ducku.bookapi.history.BorrowedBookResponse;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface BookService {

    Integer saveBook(BookRequest book, Authentication loggedInUser);

    BookResponse findById(Integer id);

    PageResponse<BookResponse> findAllBooks(int page, int size, Authentication loggedUser);

    PageResponse<BookResponse> findAllBooksByOwner(int page, int size, Authentication loggedUser);

    PageResponse<BorrowedBookResponse> findAllBorrowedBooks(int page, int size, Authentication loggedUser);

    PageResponse<BorrowedBookResponse> findAllReturnedBooks(int page, int size, Authentication loggedUser);

    Integer updateShareableStatus(Integer bookId, Authentication loggedUser);

    Integer updateArchivedStatus(Integer bookId, Authentication loggedUser);

    Integer borrowBook(Integer bookId, Authentication loggedUser);

    Integer returnBorrowedBook(Integer bookId, Authentication loggedUser);

    Integer approveReturnBorrowedBook(Integer bookId, Authentication loggedUser);

    void uploadBookCoverPicture(Integer bookId, MultipartFile file, Authentication loggedUser);
}
