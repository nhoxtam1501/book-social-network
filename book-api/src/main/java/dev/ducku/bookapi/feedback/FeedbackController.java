package dev.ducku.bookapi.feedback;

import dev.ducku.bookapi.common.PageResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("feedbacks")
@Tag(name = "Feedback")
@RequiredArgsConstructor
public class FeedbackController {
    private final FeedbackService service;

    @PostMapping
    public ResponseEntity<Integer> saveFeedback(@RequestBody @Valid FeedbackRequest feedbackRequest, Authentication loggedUser) {
        return ResponseEntity.ok(service.saveFeedback(feedbackRequest, loggedUser));
    }

    @GetMapping("/books/{bookId}")
    public ResponseEntity<PageResponse<FeedbackResponse>> findAllFeedbackByBook(@PathVariable Long bookId,
                                                                                @RequestParam(name = "page", defaultValue = "0", required = false) Integer page,
                                                                                @RequestParam(name = "size", defaultValue = "0", required = false) Integer size,
                                                                                Authentication loggedUser) {
        return ResponseEntity.ok(service.findAllFeedbackByBook(bookId, page, size, loggedUser));
    }
}
