package com.fast.fsf.books.event;

import com.fast.fsf.books.domain.BookListing;

public class BookModeratedEvent extends BookListingEvent {
    private final String moderationReason;

    public BookModeratedEvent(Object source, BookListing book, String moderationReason) {
        super(source, book);
        this.moderationReason = moderationReason;
    }

    public String getModerationReason() {
        return moderationReason;
    }
}
