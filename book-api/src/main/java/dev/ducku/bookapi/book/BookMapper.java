package dev.ducku.bookapi.book;

import dev.ducku.bookapi.file.StorageFileUtils;
import dev.ducku.bookapi.history.BookTransactionHistory;
import dev.ducku.bookapi.history.BorrowedBookResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookMapper {
    private final StorageFileUtils fileUtils;

    public Book toBook(BookRequest request) {
        return Book.builder()
                .id(request.id())
                .title(request.title())
                .isbn(request.isbn())
                .author(request.author())
                .archived(false)
                .synopsis(request.synopsis())
                .shareable(request.shareable())
                .build();
    }

    public BookResponse toBookResponse(Book book) {
        return BookResponse.builder()
                .id(book.getId())
                .author(book.getAuthor())
                .title(book.getTitle())
                .isbn(book.getIsbn())
                .rate(book.getRate())
                .synopsis(book.getSynopsis())
                .shareable(book.isShareable())
                .archived(book.isArchived())
                .cover(fileUtils.readFile(book.getBookCover()))
                .owner(book.getOwner().fullName())
                .build();
    }

    public BorrowedBookResponse toBorrowedBookResponse(BookTransactionHistory history) {
        return BorrowedBookResponse.builder()
                .id(history.getBook().getId())
                .title(history.getBook().getTitle())
                .authorName(history.getBook().getAuthor())
                .isbn(history.getBook().getIsbn())
                .rate(history.getBook().getRate())
                .returned(history.isReturned())
                .returnApproved(history.isReturnApproved())
                .build();
    }
}
