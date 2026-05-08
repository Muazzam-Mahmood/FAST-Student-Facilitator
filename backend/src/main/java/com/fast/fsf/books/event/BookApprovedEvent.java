package com.fast.fsf.books.event;

import com.fast.fsf.books.domain.BookListing;

public class BookApprovedEvent extends BookModeratedEvent {
    public BookApprovedEvent(Object source, BookListing book, String reason) { super(source, book, reason); }
}
