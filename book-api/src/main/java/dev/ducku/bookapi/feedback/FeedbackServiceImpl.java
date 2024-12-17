package dev.ducku.bookapi.feedback;

import dev.ducku.bookapi.book.Book;
import dev.ducku.bookapi.book.BookRepository;
import dev.ducku.bookapi.common.PageResponse;
import dev.ducku.bookapi.exception.OperationNotPermittedException;
import dev.ducku.bookapi.user.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class FeedbackServiceImpl implements FeedbackService {
    private final FeedbackRepository repository;
    private final BookRepository bookRepository;
    private final FeedbackMapper feedbackMapper;

    @Override
    public Integer saveFeedback(FeedbackRequest feedbackRequest, Authentication loggedUser) {
        Book book = findAndValidateBookById(feedbackRequest.bookId());
        User user = (User) loggedUser.getPrincipal();
        validateIsEquals(book.getOwner().getId(), user.getId(), "You cannot give a feedback to your own book");
        Feedback feedback = feedbackMapper.toFeedback(feedbackRequest);
        return repository.save(feedback).getId();
    }

    @Override
    public PageResponse<FeedbackResponse> findAllFeedbackByBook(Long bookId, Integer page, Integer size, Authentication loggedUser) {
        User user = (User) loggedUser.getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<Feedback> feedbacksPage = repository.findAllByBookId(bookId, pageable);
        List<FeedbackResponse> feedbackResponses = feedbacksPage
                .getContent()
                .stream()
                .map(f -> feedbackMapper.toFeedbackResponse(f, user.getId()))
                .toList();
        return PageResponse.<FeedbackResponse>builder()
                .content(feedbackResponses)
                .number(feedbacksPage.getNumber())
                .size(feedbacksPage.getSize())
                .totalElements(feedbacksPage.getTotalElements())
                .totalPages(feedbacksPage.getTotalPages())
                .first(feedbacksPage.isFirst())
                .last(feedbacksPage.isLast())
                .build();
    }

    private Book findAndValidateBookById(Integer bookId) {
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new EntityNotFoundException("No book found with ID::" + bookId));
        if (book.isArchived() || !book.isShareable()) {
            throw new OperationNotPermittedException("You cannot give a feedback for an archived or not shareable");
        }
        return book;
    }

    private void validateIsEquals(Number a, Number b, String errorMessage) {
        if (!Objects.equals(a, b)) {
            throw new OperationNotPermittedException(errorMessage);
        }
    }
}
