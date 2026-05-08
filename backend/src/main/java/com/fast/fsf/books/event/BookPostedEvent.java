package com.fast.fsf.books.event;

import com.fast.fsf.books.domain.BookListing;

public class BookPostedEvent extends BookListingEvent {
    public BookPostedEvent(Object source, BookListing book) { super(source, book); }
}
