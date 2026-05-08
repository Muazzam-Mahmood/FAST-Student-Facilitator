package com.fast.fsf.books.event;

import com.fast.fsf.books.domain.BookListing;

public class BookUpdatedEvent extends BookListingEvent {
    public BookUpdatedEvent(Object source, BookListing book) { super(source, book); }
}
