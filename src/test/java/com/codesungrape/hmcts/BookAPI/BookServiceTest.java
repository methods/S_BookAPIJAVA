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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * @ExtendWith(MockitoExtension.class): tells JUnit 5 to use Mockito's extension and automatically initializes all @Mock and @InjectMocks fields when running this test class.
 * @Mock: Creates a fake version (mock) of the dependency.
 * @InjectMocks: creates an instance of the real class under test.
 * @BeforeEach: Runs before each test method in the class.
 * @Test: Marks the method as a test case that JUnit should execute.
 *
 */


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

    // --------- TESTS ------------

    @Test
    void testCreateBook_Success() {

        // Arrange: tell the mock repository what to do when called
        when(testBookRepository.save(any(Book.class))).thenReturn(persistedBook);

        // Act: call the service method we are testing
        Book result = testBookService.createBook(validBookRequest);

        // Assert: Check the outcome
        assertNotNull(result);
        assertEquals(testId, result.getId());
        assertEquals(validBookRequest.getTitle(), result.getTitle());
        assertEquals(validBookRequest.getSynopsis(), result.getSynopsis());
        assertEquals(validBookRequest.getAuthor(), result.getAuthor());

        // Did the service perform the correct action on its dependency?
        verify(testBookRepository, times(1)).save(any(Book.class));

    }

    @Test
    void testCreateBook_NullRequest_ThrowsException() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            testBookService.createBook(null);
        });
    }

    @Test
    void testCreateBook_NullTitle_ThrowsException() {
        // Arrange
        BookRequest invalidRequest = new BookRequest(null, "Synopsis", "Author");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            testBookService.createBook(invalidRequest);
        });

        // Verify repository was never called
        verify(testBookRepository, never()).save(any());
    }

    @Test
    void testCreateBook_EmptyTitle_ThrowsException() {
        // Arrange
        BookRequest invalidRequest = new BookRequest("", "Synopsis", "Author");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            testBookService.createBook(invalidRequest);
        });
    }

    @Test
    void testCreateBook_BlankTitle_ThrowsException() {
        // Arrange
        BookRequest invalidRequest = new BookRequest("   ", "Synopsis", "Author");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            testBookService.createBook(invalidRequest);
        });
    }

    // --------- Repository failures
    @Test
    void testCreateBook_RepositoryFailure_ThrowsException() {
        // Arrange
        when(testBookRepository.save(any(Book.class)))
                .thenThrow(new RuntimeException("Database connection failed"));

        // Act & assert
        assertThrows(RuntimeException.class, () -> {
            testBookService.createBook(validBookRequest);
        });
    }

    @Test
    void testCreateBook_RepositoryReturnsNull_HandlesGracefully() {
        // Arrange
        when(testBookRepository.save(any(Book.class)))
                .thenReturn(null);

        // Act & assert
        assertThrows(IllegalStateException.class, () -> {
            testBookService.createBook(validBookRequest);
        });
    }
}