package dev.ducku.bookapi.book;

import dev.ducku.bookapi.common.PageResponse;
import dev.ducku.bookapi.exception.OperationNotPermittedException;
import dev.ducku.bookapi.file.FileStorageService;
import dev.ducku.bookapi.history.BookTransactionHistory;
import dev.ducku.bookapi.history.BookTransactionHistoryRepository;
import dev.ducku.bookapi.history.BorrowedBookResponse;
import dev.ducku.bookapi.user.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;

import static dev.ducku.bookapi.book.BookSpecification.withOwnerId;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
    private final BookRepository repository;
    private final BookMapper bookMapper;
    private final BookTransactionHistoryRepository transactionHistoryRepository;
    private final FileStorageService fileStorageService;

    @Override
    public Integer saveBook(BookRequest request, Authentication loggedInUser) {
//        User user = (User) loggedInUser.getPrincipal();
        Book book = bookMapper.toBook(request);
        //      book.setOwner(user);
        return repository.save(book).getId();
    }

    @Override
    public BookResponse findById(Integer id) {
        return repository.findById(id)
                .map(bookMapper::toBookResponse)
                .orElseThrow(() -> new EntityNotFoundException("No book found with ID:: " + id));
    }

    @Override
    public PageResponse<BookResponse> findAllBooks(int page, int size, Authentication loggedUser) {
        //User user = (User) loggedUser.getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<Book> bookPage = repository.findAllDisplayableBooks(pageable, loggedUser.getName());
        List<BookResponse> bookResponses = bookPage.stream()
                .map(bookMapper::toBookResponse)
                .toList();
        return PageResponse.<BookResponse>builder()
                .content(bookResponses)
                .number(bookPage.getNumber())
                .size(bookPage.getSize())
                .totalElements(bookPage.getTotalElements())
                .totalPages(bookPage.getTotalPages())
                .first(bookPage.isFirst())
                .last(bookPage.isLast())
                .build();
    }

    @Override
    public PageResponse<BookResponse> findAllBooksByOwner(int page, int size, Authentication loggedUser) {
        //User user = (User) loggedUser.getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<Book> bookPage = repository.findAll(withOwnerId(loggedUser.getName()), pageable);
        List<BookResponse> bookResponses = bookPage.stream()
                .map(bookMapper::toBookResponse)
                .toList();
        return PageResponse.<BookResponse>builder()
                .content(bookResponses)
                .number(bookPage.getNumber())
                .size(bookPage.getSize())
                .totalElements(bookPage.getTotalElements())
                .totalPages(bookPage.getTotalPages())
                .first(bookPage.isFirst())
                .last(bookPage.isLast())
                .build();
    }

    @Override
    public PageResponse<BorrowedBookResponse> findAllBorrowedBooks(int page, int size, Authentication loggedUser) {
        //User user = (User) loggedUser.getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<BookTransactionHistory> allBorrowedBooks = transactionHistoryRepository.findAllBorrowedBooks(loggedUser.getName(), pageable);
        List<BorrowedBookResponse> bookResponses = allBorrowedBooks.stream()
                .map(bookMapper::toBorrowedBookResponse)
                .toList();
        return PageResponse.<BorrowedBookResponse>builder()
                .content(bookResponses)
                .number(allBorrowedBooks.getNumber())
                .size(allBorrowedBooks.getSize())
                .totalElements(allBorrowedBooks.getTotalElements())
                .totalPages(allBorrowedBooks.getTotalPages())
                .first(allBorrowedBooks.isFirst())
                .last(allBorrowedBooks.isLast())
                .build();
    }

    @Override
    public PageResponse<BorrowedBookResponse> findAllReturnedBooks(int page, int size, Authentication loggedUser) {
        User user = (User) loggedUser.getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<BookTransactionHistory> allBorrowedBooks = transactionHistoryRepository.findAllReturnedBooks(loggedUser.getName(), pageable);
        List<BorrowedBookResponse> bookResponses = allBorrowedBooks.stream()
                .map(bookMapper::toBorrowedBookResponse)
                .toList();
        return PageResponse.<BorrowedBookResponse>builder()
                .content(bookResponses)
                .number(allBorrowedBooks.getNumber())
                .size(allBorrowedBooks.getSize())
                .totalElements(allBorrowedBooks.getTotalElements())
                .totalPages(allBorrowedBooks.getTotalPages())
                .first(allBorrowedBooks.isFirst())
                .last(allBorrowedBooks.isLast())
                .build();
    }

    @Override
    public Integer updateShareableStatus(Integer bookId, Authentication loggedUser) {
        Book book = repository.findById(bookId).orElseThrow(() -> new EntityNotFoundException("No book found with ID::" + bookId));
        User user = (User) loggedUser.getPrincipal();
        if (book.getCreatedBy().equals(loggedUser.getName())) {
            book.setShareable(!book.isShareable());
            repository.save(book);
            return bookId;
        }
        throw new OperationNotPermittedException("You cannot update others book shareable status");
    }

    @Override
    public Integer updateArchivedStatus(Integer bookId, Authentication loggedUser) {
        Book book = repository.findById(bookId).orElseThrow(() -> new EntityNotFoundException("No book found with ID::" + bookId));
        User user = (User) loggedUser.getPrincipal();
        if (book.getCreatedBy().equals(loggedUser.getName())) {
            book.setArchived(!book.isArchived());
            repository.save(book);
            return bookId;
        }
        throw new OperationNotPermittedException("You cannot update others book archived status");
    }

    @Override
    public Integer borrowBook(Integer bookId, Authentication loggedUser) {
        Book book = findAndValidateBookById(bookId);
        User user = (User) loggedUser.getPrincipal();
        if (!Objects.equals(book.getCreatedBy(), loggedUser.getName())) {
            throw new OperationNotPermittedException("You cannot borrow your own book");
        }
        final boolean isAlreadyBorrowedByLoggedUser = transactionHistoryRepository.isAlreadyBorrowedByUser(bookId, loggedUser.getName());
        if (isAlreadyBorrowedByLoggedUser) {
            throw new OperationNotPermittedException("You already borrowed this book and it is still not returned or the return is not approved by the owner");
        }

        final boolean isAlreadyBorrowedByOtherUser = transactionHistoryRepository.isAlreadyBorrowed(bookId);
        if (isAlreadyBorrowedByOtherUser) {
            throw new OperationNotPermittedException("The requested book is already be borrowed");
        }
        BookTransactionHistory transactionHistory = BookTransactionHistory.builder()
                //.user(user)
                .userId(loggedUser.getName())
                .book(book)
                .returned(false)
                .returnApproved(false)
                .build();
        return transactionHistoryRepository.save(transactionHistory).getId();
    }

    @Override
    public Integer returnBorrowedBook(Integer bookId, Authentication loggedUser) {
        Book book = findAndValidateBookById(bookId);
        User user = (User) loggedUser.getPrincipal();
//        if (!Objects.equals(book.getOwner().getId(), loggedUser.getName())) {
//            throw new OperationNotPermittedException("You cannot borrow or return your own book");
//        }
        validateIsEquals(book.getCreatedBy(), loggedUser.getName(), "You cannot borrow or return your own book");
        BookTransactionHistory transactionHistory = transactionHistoryRepository.findByBookIdAndUserId(bookId, loggedUser.getName())
                .orElseThrow(() -> new OperationNotPermittedException("You did not borrow this book"));
        transactionHistory.setReturned(true);
        return transactionHistoryRepository.save(transactionHistory).getId();
    }

    @Override
    public Integer approveReturnBorrowedBook(Integer bookId, Authentication loggedUser) {
        Book book = findAndValidateBookById(bookId);
        User user = (User) loggedUser.getPrincipal();
//        if (!Objects.equals(book.getCreatedBy(), loggedUser.getName())) {
//            throw new OperationNotPermittedException("You cannot approve the book that you do not own");
//        }
        validateIsEquals(book.getCreatedBy(), loggedUser.getName(), "You cannot approve the book that you do not own");
        BookTransactionHistory transactionHistory = transactionHistoryRepository.findByBookIdAndOwnerId(bookId, loggedUser.getName())
                .orElseThrow(() -> new OperationNotPermittedException("The book is not returned yet."));
        transactionHistory.setReturnApproved(true);
        return transactionHistoryRepository.save(transactionHistory).getId();
    }

    private Book findAndValidateBookById(Integer bookId) {
        Book book = repository.findById(bookId).orElseThrow(() -> new EntityNotFoundException("No book found with ID::" + bookId));
        if (book.isArchived() || !book.isShareable()) {
            throw new OperationNotPermittedException("You cannot borrow this book since it is archived or not shareable");
        }
        return book;
    }

    private void validateIsEquals(CharSequence a, CharSequence b, String errorMessage) {
        if (!Objects.equals(a, b)) {
            throw new OperationNotPermittedException(errorMessage);
        }
    }

    @Override
    public void uploadBookCoverPicture(Integer bookId, MultipartFile file, Authentication loggedUser) {
        Book book = findAndValidateBookById(bookId);
        //User user = (User) loggedUser.getPrincipal();
        var bookCover = fileStorageService.saveFile(file, bookId);
        book.setBookCover(bookCover);
        repository.save(book);
    }
}
