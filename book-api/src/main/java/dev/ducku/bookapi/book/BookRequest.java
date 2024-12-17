package dev.ducku.bookapi.book;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record BookRequest(
        Integer id,
        @NotNull(message = "100")
        @NotEmpty(message = "100")
        @NotBlank(message = "100")
        String title,
        @NotNull(message = "101")
        @NotEmpty(message = "101")
        @NotBlank(message = "101")
        String author,
        @NotNull(message = "102")
        @NotEmpty(message = "102")
        @NotBlank(message = "102")
        String isbn,
        @NotNull(message = "103")
        @NotEmpty(message = "103")
        @NotBlank(message = "103")
        String synopsis,
        boolean shareable) {
}
