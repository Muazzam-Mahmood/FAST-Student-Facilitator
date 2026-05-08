package com.fast.fsf.books.factory;

import com.fast.fsf.books.domain.BookListing;
import org.springframework.stereotype.Component;

/**
 * Factory Method pattern (GoF): centralises construction rules for Book Listings.
 */
@Component
public class BookListingFactory {

    public BookListing createPendingListing(BookListing incoming) {
        incoming.setApproved(false);
        incoming.setStatus("ACTIVE");
        return incoming;
    }

    public BookListing createUpdatedListing(BookListing existing, BookListing updated) {
        existing.setBookTitle(updated.getBookTitle());
        existing.setAuthor(updated.getAuthor());
        existing.setCourseCode(updated.getCourseCode());
        existing.setBookCondition(updated.getBookCondition());
        existing.setPrice(updated.getPrice());
        existing.setFrontCoverImage(updated.getFrontCoverImage());
        existing.setBackCoverImage(updated.getBackCoverImage());
        existing.setListingType(updated.getListingType());
        
        // Re-require approval after edit
        existing.setApproved(false);
        return existing;
    }
}
