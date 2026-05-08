package com.fast.fsf.books.event;

import com.fast.fsf.books.domain.BookListing;
import org.springframework.context.ApplicationEvent;

public class BookListingEvent extends ApplicationEvent {
    private final BookListing book;

    public BookListingEvent(Object source, BookListing book) {
        super(source);
        this.book = book;
    }

    public BookListing getBook() {
        return book;
    }
}
