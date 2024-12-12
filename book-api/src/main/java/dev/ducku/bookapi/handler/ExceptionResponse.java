package dev.ducku.bookapi.handler;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.springframework.http.HttpStatus;

import java.util.Map;
import java.util.Set;

import static org.springframework.http.HttpStatus.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ExceptionResponse {
    private Integer code;
    private String description;
    private String error;
    private Set<String> validationErrors;
    private Map<String, String> errors;

    @Getter
    public enum BUSINESS_ERROR_CODE {
        NO_CODE(0, NOT_IMPLEMENTED, "No code"),
        INCORRECT_CURRENT_PASSWORD(300, BAD_REQUEST, "Current password is incorrect"),
        NEW_PASSWORD_DOES_NOT_MATCH(301, BAD_REQUEST, "The new password does not match"),
        ACCOUNT_LOCKED(302, FORBIDDEN, "User account is locked"),
        ACCOUNT_DISABLED(303, FORBIDDEN, "User account is disabled"),
        BAD_CREDENTIALS(304, FORBIDDEN, "Login and / or password is incorrect"),
        ;

        private final int code;
        private final String description;
        private final HttpStatus httpStatus;

        BUSINESS_ERROR_CODE(int code, HttpStatus httpStatus, String description) {
            this.code = code;
            this.description = description;
            this.httpStatus = httpStatus;
        }
    }
}
