package dev.ducku.bookapi.feedback;

import dev.ducku.bookapi.common.PageResponse;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface FeedbackService {

    Integer saveFeedback(FeedbackRequest feedbackRequest, Authentication loggedUser);

    PageResponse<FeedbackResponse> findAllFeedbackByBook(Long bookId, Integer page, Integer size, Authentication loggedUser);
}
