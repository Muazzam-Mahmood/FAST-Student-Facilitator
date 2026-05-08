package com.fast.fsf.books.event;

import com.fast.fsf.books.domain.BookListing;

public class BookFlaggedEvent extends BookModeratedEvent {
    public BookFlaggedEvent(Object source, BookListing book, String reason) { super(source, book, reason); }
}
