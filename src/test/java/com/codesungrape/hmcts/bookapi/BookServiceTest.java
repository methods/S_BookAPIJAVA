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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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

    // CoPilot feedback:
    // This test will fail because BookRequest uses @value from Lombok with @notblank validation.
    // The @notblank constraint on the title field means that creating a BookRequest with a null
    // title should trigger validation failure at the DTO level, not allow the object to be
    // created. Either the test expectations are incorrect, or the DTO validation is not being
    // applied. The same issue affects tests on lines 105-116, 119-127, and 130-138.

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

        // Assert
        assertNotNull(result);
        assertEquals(testId, result.getId());
        assertEquals(specialRequest.title(), result.getTitle());
        assertEquals(specialRequest.synopsis(), result.getSynopsis());
        assertEquals(specialRequest.author(), result.getAuthor());

        // Did the service perform the correct action on its dependency?
        verify(testBookRepository, times(1)).save(any(Book.class));
    }

    // --------------------------------------------------------------------------------------------
    // Tests: deleteBookById(UUID)
    // -------------------------------------------------------------------------------------------

    @Test
    void testDelete_Book_ShouldThrowException_WhenIdNotFound() {

        // Arrange: As goal is to test what happens when the resource doesn't exist,
        // we intentionally simulate DB returning NO result
        when(testBookRepository.findById(testId)).thenReturn(Optional.empty());

        // ACT and ASSERT: throw ResourceNotFoundException when calling the delete method.
        assertThrows(
            // custom exception to reflect business rules vs technical problem
            ResourceNotFoundException.class,
            () -> testBookService.deleteBookById(testId)
        );

        // Assert: ensure the save method was NEVER called.
        // proves delete business logic halts immediately when the resource isn't found.
        verify(testBookRepository, never()).save(any(Book.class));
    }

    @Test
    void testDeleteBookById_Success() {

        // Arrange:
        persistedBook.setDeleted(false); // ensure starting state

        when(testBookRepository.findById(testId))
            .thenReturn(Optional.of(persistedBook));

        when(testBookRepository.save(any(Book.class)))
            .thenReturn(persistedBook);

        // Act: call the service method we are testing
        testBookService.deleteBookById(testId);

        // Assert: the entity was marked deleted
        assertTrue(persistedBook.isDeleted());

        // Assert: repository methods were called correctly
        verify(testBookRepository, times(1)).findById(testId);
        verify(testBookRepository, times(1)).save(persistedBook);
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
        // Verify save was NEVER called (it entered the 'false' branch of if)
        verify(testBookRepository, never()).save(any(Book.class));
    }
}
