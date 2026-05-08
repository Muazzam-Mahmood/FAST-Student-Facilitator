package com.fast.fsf.books.event;

import com.fast.fsf.books.domain.BookListing;

public class BookResolvedEvent extends BookListingEvent {
    public BookResolvedEvent(Object source, BookListing book) { super(source, book); }
}
