package com.fast.fsf.books.event;

import com.fast.fsf.books.domain.BookListing;

public class BookDeletedEvent extends BookModeratedEvent {
    public BookDeletedEvent(Object source, BookListing book, String reason) { super(source, book, reason); }
}
