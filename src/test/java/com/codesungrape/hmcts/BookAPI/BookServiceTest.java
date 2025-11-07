package com.codesungrape.hmcts.BookAPI;

import com.codesungrape.hmcts.BookAPI.dto.BookRequest;
import com.codesungrape.hmcts.BookAPI.entity.Book;
import com.codesungrape.hmcts.BookAPI.repository.BookRepository;
import com.codesungrape.hmcts.BookAPI.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.provider.Arguments;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.Assert;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.Arguments;
import java.util.stream.Stream;

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

    // ----- EDGE cases ---------

    @ParameterizedTest(name= "{0}") // Display the test name
    @MethodSource("provideLongFieldTestCases")
    void testCreateBook_VeryLongFields_Success(String testName, BookRequest request, Book expectedBook) {

        // Arrange
        when(testBookRepository.save(any(Book.class)))
                .thenReturn(expectedBook);

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

//    @ParameterizedTest
//    @CsvSource({
//            "500, title, 'A very long title test'",
//            "1000, synopsis, 'A very long synopsis test'"
//    })
//    void testCreateBook_VeryLongFields_Success(int repeatCount, String fieldType, String description) {
//
//        // Arrange
//        String longText = "A".repeat(repeatCount);
//
//        BookRequest request;
//        Book expectedBook;
//
//        if (fieldType.equals("title")) {
//            request = new BookRequest(longText, "Synopsis", "Author");
//            expectedBook = Book.builder()
//                    .id(testId)
//                    .title(longText)
//                    .synopsis("Synopsis")
//                    .author("Author")
//                    .build();
//        } else {
//            request = new BookRequest("Title", longText, "Author");
//            expectedBook = Book.builder()
//                    .id(testId)
//                    .title("Title")
//                    .synopsis(longText)
//                    .author("Author")
//                    .build();
//        }
//
//        when(testBookRepository.save(any(Book.class)))
//                .thenReturn(expectedBook);
//
//        // Act
//        Book result = testBookService.createBook(request);
//
//        // Assert
//        assertNotNull(result);
//        assertEquals(testId, result.getId());
//
//        if (fieldType.equals("title")) {
//            assertEquals(longText, result.getTitle());
//        } else {
//            assertEquals(longText, result.getSynopsis());
//        }
//
//        verify(testBookRepository, times(1)).save(any(Book.class));
//    }

//    @Test
//    void testCreateBook_VeryLongTitle_Success() {
//        // Arrange
//        String longTitle = "A".repeat(500);
//        BookRequest longTitleRequest = new BookRequest(longTitle, "Synopsis", "Author");
//
//        Book expectedBook = Book.builder()
//                .id(testId)
//                .title(longTitle)
//                .synopsis("Synopsis")
//                .author("Author")
//                .build();
//
//        when(testBookRepository.save(any(Book.class)))
//                .thenReturn(expectedBook);
//
//        // Act
//        Book result = testBookService.createBook(longTitleRequest);
//
//        // Assert
//        assertNotNull(result);
//        assertEquals(testId, result.getId());
//        assertEquals(longTitle, result.getTitle());
//        assertEquals(expectedBook.getSynopsis(), result.getSynopsis());
//        assertEquals(expectedBook.getAuthor(), result.getAuthor());
//
//        // Did the service perform the correct action on its dependency?
//        verify(testBookRepository, times(1)).save(any(Book.class));
//
//    }
//
//    @Test
//    void testCreateBook_VeryLongSynopsis_Success() {
//
//        // Arrange
//        String longSynopsis = "A".repeat(1000);
//        BookRequest longSynopsisRequest = new BookRequest("Title", longSynopsis, "Author");
//
//        Book expectedBook = Book.builder()
//                .id(testId)
//                .title("Title")
//                .synopsis(longSynopsis)
//                .author("Author")
//                .build();
//
//        when(testBookRepository.save(any(Book.class)))
//                .thenReturn(expectedBook);
//
//        // Act
//        Book result = testBookService.createBook(longSynopsisRequest);
//
//        // Assert
//        assertEquals(longSynopsis, result.getSynopsis());
//
//    }

    @Test
    void testCreateBook_SpecialCharactersInTitle_Success() {
        // Arrange
        BookRequest specialRequest = new BookRequest(
                "Test: A Book! @#$%^&*()",
                "Synopsis",
                "Author"
        );

        Book expectedBook = Book.builder()
                .id(testId)
                .title(specialRequest.getTitle())
                .synopsis(specialRequest.getSynopsis())
                .author(specialRequest.getAuthor())
                .build();

        when(testBookRepository.save(any(Book.class)))
                .thenReturn(expectedBook);

        // Act
        Book result = testBookService.createBook(specialRequest);

        // Assert
        assertNotNull(result);
        assertEquals(testId, result.getId());
        assertEquals(specialRequest.getTitle(), result.getTitle());
        assertEquals(specialRequest.getSynopsis(), result.getSynopsis());
        assertEquals(specialRequest.getAuthor(), result.getAuthor());

        // Did the service perform the correct action on its dependency?
        verify(testBookRepository, times(1)).save(any(Book.class));
    }
}