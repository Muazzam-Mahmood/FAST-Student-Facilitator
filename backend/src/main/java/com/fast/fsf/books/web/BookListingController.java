package com.fast.fsf.books.web;

import com.fast.fsf.books.domain.BookListing;
import com.fast.fsf.books.factory.BookListingFactory;
import com.fast.fsf.books.event.*;
import com.fast.fsf.books.persistence.BookListingRepository;
import com.fast.fsf.analytics.service.FeatureUsageAnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/books")
@CrossOrigin(originPatterns = {"http://localhost:*"})
public class BookListingController {

    private final BookListingRepository bookRepository;
    private final BookListingFactory bookFactory;
    private final ApplicationEventPublisher eventPublisher;
    private final FeatureUsageAnalyticsService analyticsService;

    public BookListingController(BookListingRepository bookRepository, BookListingFactory bookFactory, ApplicationEventPublisher eventPublisher, FeatureUsageAnalyticsService analyticsService) {
        this.bookRepository = bookRepository;
        this.bookFactory = bookFactory;
        this.eventPublisher = eventPublisher;
        this.analyticsService = analyticsService;
    }

    @GetMapping
    public List<BookListing> getAllBooks(@RequestParam(required = false, defaultValue = "SELL") String type) {
        analyticsService.logActivity("Books");
        return bookRepository.findByListingTypeAndApprovedTrueOrderByStatusAsc(type);
    }

    @GetMapping("/my")
    public List<BookListing> getMyBooks(@RequestParam String email) {
        return bookRepository.findByOwnerEmail(email);
    }

    @PostMapping
    public ResponseEntity<BookListing> createBookListing(@RequestBody BookListing book) {
        BookListing pendingBook = bookFactory.createPendingListing(book);
        BookListing saved = bookRepository.save(pendingBook);
        
        eventPublisher.publishEvent(new BookPostedEvent(this, saved));
        
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookListing> updateBookListing(@PathVariable Long id, @RequestBody BookListing updatedBook) {
        return bookRepository.findById(id).map(book -> {
            BookListing modifiedBook = bookFactory.createUpdatedListing(book, updatedBook);
            BookListing saved = bookRepository.save(modifiedBook);
            
            eventPublisher.publishEvent(new BookUpdatedEvent(this, saved));
            return ResponseEntity.ok(saved);
        }).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/pending")
    public List<BookListing> getPendingBooks() {
        return bookRepository.findByApprovedFalse();
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<BookListing> approveBook(@PathVariable Long id, @RequestParam(required = false) String reason) {
        return bookRepository.findById(id).map(book -> {
            book.setApproved(true);
            book.setModerationReason(reason);
            BookListing saved = bookRepository.save(book);
            
            eventPublisher.publishEvent(new BookApprovedEvent(this, saved, reason));
            return ResponseEntity.ok(saved);
        }).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/flagged/count")
    public long getFlaggedCount() {
        return bookRepository.countByFlaggedTrue();
    }

    @GetMapping("/flagged")
    public List<BookListing> getFlaggedBooks() {
        return bookRepository.findByFlaggedTrue();
    }

    @PutMapping("/{id}/flag")
    public ResponseEntity<BookListing> flagBook(@PathVariable Long id, @RequestParam(required = false) String reason) {
        return bookRepository.findById(id).map(book -> {
            book.setFlagged(true);
            book.setModerationReason(reason);
            BookListing saved = bookRepository.save(book);
            
            eventPublisher.publishEvent(new BookFlaggedEvent(this, saved, reason));
            return ResponseEntity.ok(saved);
        }).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/resolve")
    public ResponseEntity<BookListing> resolveBook(@PathVariable Long id) {
        return bookRepository.findById(id).map(book -> {
            book.setFlagged(false);
            book.setModerationReason(null);
            BookListing saved = bookRepository.save(book);
            
            eventPublisher.publishEvent(new BookResolvedEvent(this, saved));
            return ResponseEntity.ok(saved);
        }).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/close")
    public ResponseEntity<BookListing> closeBookListing(@PathVariable Long id) {
        return bookRepository.findById(id).map(book -> {
            book.setStatus("CLOSED");
            BookListing saved = bookRepository.save(book);
            return ResponseEntity.ok(saved);
        }).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/count/active")
    public long getActiveCount() {
        return bookRepository.countByApprovedTrue();
    }

    @GetMapping("/search")
    public List<BookListing> searchBooks(@RequestParam String query, @RequestParam(defaultValue = "SELL") String type) {
        analyticsService.logActivity("Books");
        return bookRepository.searchApprovedListings(query, type);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookListing> getBook(@PathVariable Long id) {
        Optional<BookListing> book = bookRepository.findById(id).filter(BookListing::isApproved);
        return book.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id, @RequestParam(required = false) String reason) {
        return bookRepository.findById(id).map(book -> {
            eventPublisher.publishEvent(new BookDeletedEvent(this, book, reason));
            bookRepository.deleteById(id);
            return ResponseEntity.ok().<Void>build();
        }).orElse(ResponseEntity.notFound().build());
    }
}
