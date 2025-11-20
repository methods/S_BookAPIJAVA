package com.codesungrape.hmcts.bookapi;

import com.codesungrape.hmcts.bookapi.dto.BookRequest;
import com.codesungrape.hmcts.bookapi.entity.Book;
import com.codesungrape.hmcts.bookapi.exception.ResourceNotFoundException;
import com.codesungrape.hmcts.bookapi.repository.BookRepository;
import com.codesungrape.hmcts.bookapi.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;


/**.
 * Explains how this test class uses Mockito:
 * - JUnit is extended using MockitoExtension
 * - @Mock creates fake dependencies
 * - @InjectMocks creates the real service with mocks injected
 * - @BeforeEach runs before every test
 * - @Test marks a test method
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

    // --------------------------------------
    // Parameter Sources
    // --------------------------------------

    // Provide test data, static method: can be called without creating an object.
    private static Stream<Arguments> provideLongFieldTestCases() {
        UUID testId = UUID.randomUUID();

        String longTitle = "A".repeat(500);
        String longSynopsis = "A".repeat(1000);

        return Stream.of(
            Arguments.of(
                "Very long title (500 chars)",
                new BookRequest(longTitle, "Synopsis", "Author"),
                Book.builder()
                    .id(testId)
                    .title(longTitle)
                    .synopsis("Synopsis")
                    .author("Author")
                    .build()
            ),
            Arguments.of(
                "Very long synopsis (1000 chars)",
                new BookRequest("Title", longSynopsis, "Author"),
                Book.builder()
                    .id(testId)
                    .title("Title")
                    .synopsis(longSynopsis)
                    .author("Author")
                    .build()
            )
        );
    }

    // --------------------------------------
    // Tests
    // --------------------------------------
    @BeforeEach
    void setUp() {
        testId = UUID.randomUUID();

        validBookRequest =
            new BookRequest(
                "The Great Java Gatsby",
                "A story about unit testing and wealth.",
                "F. Scott Spring"
            );

        // This simulates a Book object as it would look coming back from the DB
        persistedBook =
            Book.builder()
                .id(testId)
                .title(validBookRequest.title())
                .synopsis(validBookRequest.synopsis())
                .author(validBookRequest.author())
                .deleted(false)
                .createdAt(java.time.Instant.now())
                .build();
    }

    // --------------------------------------
    // Tests: createBook
    // --------------------------------------
    @Test
    void testCreateBook_Success() {

        // Arrange: tell the mock repository what to do when called
        when(testBookRepository.save(any(Book.class))).thenReturn(persistedBook);

        // Act: call the service method we are testing
        Book result = testBookService.createBook(validBookRequest);

        // Assert: Check the outcome
        assertNotNull(result);
        assertEquals(testId, result.getId());
        assertEquals(validBookRequest.title(), result.getTitle());
        assertEquals(validBookRequest.synopsis(), result.getSynopsis());
        assertEquals(validBookRequest.author(), result.getAuthor());

        // Did the service perform the correct action on its dependency?
        verify(testBookRepository, times(1)).save(any(Book.class));
    }

    @Test
    void testCreateBook_NullRequest_ThrowsException() {
        // Act & Assert
        assertThrows(
            NullPointerException.class,
            () -> {
                testBookService.createBook(null);
            }
        );
    }

    @Test
    void testCreateBook_NullTitle_ThrowsException() {
        // Arrange
        BookRequest invalidRequest = new BookRequest(null, "Synopsis", "Author");

        // Act & Assert
        assertThrows(
            IllegalArgumentException.class,
            () -> {
                testBookService.createBook(invalidRequest);
            }
        );

        // Verify repository was never called
        verify(testBookRepository, never()).save(any());
    }

    @Test
    void testCreateBook_EmptyTitle_ThrowsException() {
        // Arrange
        BookRequest invalidRequest = new BookRequest("", "Synopsis", "Author");

        // Act & Assert
        assertThrows(
            IllegalArgumentException.class,
            () -> {
                testBookService.createBook(invalidRequest);
            }
        );
    }

    @Test
    void testCreateBook_BlankTitle_ThrowsException() {
        // Arrange
        BookRequest invalidRequest = new BookRequest("   ", "Synopsis", "Author");

        // Act & Assert
        assertThrows(
            IllegalArgumentException.class,
            () -> {
                testBookService.createBook(invalidRequest);
            }
        );
    }

    @Test
    void testCreateBook_RepositoryFailure_ThrowsException() {
        // Arrange
        when(testBookRepository.save(any(Book.class)))
            .thenThrow(new RuntimeException("Database connection failed"));

        // Act & assert
        assertThrows(
            RuntimeException.class,
            () -> {
                testBookService.createBook(validBookRequest);
            }
        );
    }

    // ----- EDGE cases ---------

    @ParameterizedTest(name = "{0}") // Display the test name
    @MethodSource("provideLongFieldTestCases")
    void testCreateBook_VeryLongFields_Success(
        String testName,
        BookRequest request,
        Book expectedBook
    ) {

        // Arrange
        when(testBookRepository.save(any(Book.class))).thenReturn(expectedBook);

        // Act
        Book result = testBookService.createBook(request);

        // Assert
        assertNotNull(result);
        assertEquals(expectedBook.getId(), result.getId());
        assertEquals(expectedBook.getTitle(), result.getTitle());
        assertEquals(expectedBook.getSynopsis(), result.getSynopsis());
        assertEquals(expectedBook.getAuthor(), result.getAuthor());

        verify(testBookRepository, times(1)).save(any(Book.class));
    }

    @Test
    void testCreateBook_SpecialCharactersInTitle_Success() {
        // Arrange
        BookRequest specialRequest =
            new BookRequest("Test: A Book! @#$%^&*()", "Synopsis", "Author");

        Book expectedBook =
            Book.builder()
                .id(testId)
                .title(specialRequest.title())
                .synopsis(specialRequest.synopsis())
                .author(specialRequest.author())
                .build();

        when(testBookRepository.save(any(Book.class))).thenReturn(expectedBook);

        // Act
        Book result = testBookService.createBook(specialRequest);

        // Assert: capture the Book passed to save()
        ArgumentCaptor<Book> bookCaptor = ArgumentCaptor.forClass(Book.class);
        verify(testBookRepository, times(1)).save(bookCaptor.capture());
        Book savedBook = bookCaptor.getValue();

        // Assert
        assertNotNull(savedBook);
        assertNull(savedBook.getId(), "ID should be null before DB generates it");
        assertEquals(specialRequest.title(), savedBook.getTitle());
        assertEquals(specialRequest.synopsis(), savedBook.getSynopsis());
        assertEquals(specialRequest.author(), savedBook.getAuthor());

        // Assert: Verify the Service fulfills its return contract
        assertEquals(expectedBook, result, "Service must return the object returned by the repository");

    }

    // --------------------------------------------------------------------------------------------
    // Tests: deleteBookById(UUID)
    // -------------------------------------------------------------------------------------------

    @Test
    void testDelete_Book_ShouldThrowException_WhenIdNotFound() {

        // Arrange: simulate missing record
        when(testBookRepository.findById(testId)).thenReturn(Optional.empty());

        // ACT and ASSERT: correct exception thrown
        assertThrows(
            ResourceNotFoundException.class,
            () -> testBookService.deleteBookById(testId)
        );

        // Assert: ensure the save method was NEVER called.
        // proves delete business logic halts immediately when the resource isn't found.
        verify(testBookRepository, never()).save(any(Book.class));
    }

    @Test
    void testDeleteBookById_Success() {

        // Arrange: ensure the test book is active
        persistedBook.setDeleted(false);

        when(testBookRepository.findById(testId))
            .thenReturn(Optional.of(persistedBook));

        // Act: call the service method we are testing
        testBookService.deleteBookById(testId);

        // Assert: the entity was marked deleted
        ArgumentCaptor<Book> bookCaptor = ArgumentCaptor.forClass(Book.class);
        verify(testBookRepository, times(1)).save(bookCaptor.capture());

        // extract from captured object (internal list)
        Book savedBook = bookCaptor.getValue();

        // Assert: the service correctly marked it as deleted
        assertTrue(
            savedBook.isDeleted(),
            "Book passed to save() should be marked deleted"
        );
        // Assert: it is the same ID we attempted to delete
        assertEquals(testId, savedBook.getId());

    }

    @Test
    void testDeleteBookById_ShouldDoNothing_WhenAlreadyDeleted() {

        // Arrange:
        persistedBook.setDeleted(true); // ensure starting state
        when(testBookRepository.findById(testId))
            .thenReturn(Optional.of(persistedBook));

        // Act: call the service method we are testing
        testBookService.deleteBookById(testId);

        // Assert
        // Verify save was NEVER called (the if condition was false, so the if block was skipped)
        verify(testBookRepository, never()).save(any(Book.class));
    }
}
