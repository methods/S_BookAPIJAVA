package com.codesungrape.hmcts.BookAPI;

import com.codesungrape.hmcts.BookAPI.dto.BookRequest;
import com.codesungrape.hmcts.BookAPI.entity.Book;
import com.codesungrape.hmcts.BookAPI.repository.BookRepository;
import com.codesungrape.hmcts.BookAPI.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import java.util.UUID;

// Annotation tells JUnit to use Mockito
@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    // Arrange: Mock a fake BookRepository
    @Mock
    private BookRepository testBookRepository;

    // Service to Test: Real service with fake repo injected
    @InjectMocks
    private BookService testBookService;

    // Test data setup (HMCTS naming consistency enforced)
    private BookRequest validBookRequest;
    private Book persistedBook;
    private UUID testId;

    @BeforeEach
    void setUp() {
        testId = UUID.randomUUID();

        validBookRequest = new BookRequest(
                "The Great Java Gatsby",
                "A story about unit testing and wealth.",
                "F. Scott Spring"
        );

        // This simulates a Book object as it would look coming back from the DB
        persistedBook = Book.builder()
                .id(testId)
                .title(validBookRequest.getTitle())
                .synopsis(validBookRequest.getSynopsis())
                .author(validBookRequest.getAuthor())
                .deleted(false)
                .createdAt(java.time.Instant.now())
                .build();
    }

}