package dev.ducku.bookapi.book;

import dev.ducku.bookapi.common.BaseEntity;
import dev.ducku.bookapi.feedback.Feedback;
import dev.ducku.bookapi.history.BookTransactionHistory;
import dev.ducku.bookapi.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "books")
public class Book extends BaseEntity {
    private String title;
    private String author;
    private String isbn;
    private String synopsis;
    private String bookCover;
    private boolean archived;
    private boolean shareable;

//    @ManyToOne
//    @JoinColumn(name = "owner_id")
//    private User owner;

    @OneToMany(mappedBy = "book")
    private List<Feedback> feedbacks;

    @OneToMany(mappedBy = "book")
    private List<BookTransactionHistory> histories;


    @Transient
    public double getRate() {
        if (feedbacks == null || feedbacks.isEmpty()) {
            return 0.0;
        }
        var rate = feedbacks.stream()
                .mapToDouble(Feedback::getNote)
                .average()
                .orElse(0.00);
        return Math.round(rate * 10.0) / 10.0; //4.23 -> 4.2
    }
}
