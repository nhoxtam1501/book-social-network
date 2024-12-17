package dev.ducku.bookapi.book;

import dev.ducku.bookapi.common.PageResponse;
import dev.ducku.bookapi.history.BorrowedBookResponse;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("books")
@Tag(name = "Book")
@RequiredArgsConstructor
public class BookController {
    private final BookService service;

    @PostMapping
    public ResponseEntity<Integer> saveBook(@RequestBody @Valid BookRequest book, Authentication loggedUser) {
        return ResponseEntity.ok(service.saveBook(book, loggedUser));
    }

    @GetMapping("/{bookId}")
    public ResponseEntity<BookResponse> findBookById(@PathVariable Integer bookId) {
        return ResponseEntity.ok(service.findById(bookId));
    }

    @GetMapping
    public ResponseEntity<PageResponse<BookResponse>> findAllBooks
            (@RequestParam(name = "page", defaultValue = "0", required = false) int page,
             @RequestParam(name = "size", defaultValue = "10", required = false) int size,
             Authentication loggedUser) {
        return ResponseEntity.ok(service.findAllBooks(page, size, loggedUser));
    }

    @GetMapping("/owner")
    public ResponseEntity<PageResponse<BookResponse>> findAllBooksByOwner
            (@RequestParam(name = "page", defaultValue = "0", required = false) int page,
             @RequestParam(name = "size", defaultValue = "10", required = false) int size,
             Authentication loggedUser) {
        return ResponseEntity.ok(service.findAllBooksByOwner(page, size, loggedUser));
    }

    @GetMapping("/borrowed")
    public ResponseEntity<PageResponse<BorrowedBookResponse>> findAllBorrowedBooks
            (@RequestParam(name = "page", defaultValue = "0", required = false) int page,
             @RequestParam(name = "size", defaultValue = "10", required = false) int size,
             Authentication loggedUser) {
        return ResponseEntity.ok(service.findAllBorrowedBooks(page, size, loggedUser));
    }


    @GetMapping("/returned")
    public ResponseEntity<PageResponse<BorrowedBookResponse>> findAllReturnedBooks
            (@RequestParam(name = "page", defaultValue = "0", required = false) int page,
             @RequestParam(name = "size", defaultValue = "10", required = false) int size,
             Authentication loggedUser) {
        return ResponseEntity.ok(service.findAllReturnedBooks(page, size, loggedUser));
    }

    @PatchMapping("/shareable/{bookId}")
    public ResponseEntity<Integer> updateShareableStatus(@PathVariable Integer bookId, Authentication loggedUser) {
        return ResponseEntity.ok(service.updateShareableStatus(bookId, loggedUser));
    }

    @PatchMapping("/archived/{bookId}")
    public ResponseEntity<Integer> updateArchivedStatus(@PathVariable Integer bookId, Authentication loggedUser) {
        return ResponseEntity.ok(service.updateArchivedStatus(bookId, loggedUser));
    }

    @PostMapping("/borrow/{bookId}")
    public ResponseEntity<Integer> borrowBook(@PathVariable Integer bookId, Authentication loggedUser) {
        return ResponseEntity.ok(service.borrowBook(bookId, loggedUser));
    }

    @PatchMapping("/borrow/return/{bookId}")
    public ResponseEntity<Integer> returnBorrowedBook(@PathVariable Integer bookId, Authentication loggedUser) {
        return ResponseEntity.ok(service.returnBorrowedBook(bookId, loggedUser));
    }

    @PatchMapping("/borrow/return/approve/{bookId}")
    public ResponseEntity<Integer> approveReturnBorrowedBook(@PathVariable Integer bookId, Authentication loggedUser) {
        return ResponseEntity.ok(service.approveReturnBorrowedBook(bookId, loggedUser));
    }


    @PostMapping(value = "/cover/{bookId}", consumes = "multipart/form-data")
    public ResponseEntity<?> uploadBookCover(@PathVariable Integer bookId,
                                             @Parameter() @RequestPart("file") MultipartFile file, Authentication loggedUser) {
        service.uploadBookCoverPicture(bookId, file, loggedUser);
        return ResponseEntity.accepted().build();
    }
}
