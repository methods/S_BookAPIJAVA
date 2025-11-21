package com.codesungrape.hmcts.bookapi;

import com.codesungrape.hmcts.bookapi.controllers.BookController;
import com.codesungrape.hmcts.bookapi.dto.BookRequest;
import com.codesungrape.hmcts.bookapi.entity.Book;
import com.codesungrape.hmcts.bookapi.service.BookService;
import com.fasterxml.jackson.databind.ObjectMapper; // Import this
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookController.class) // TDD so doesnt exist yet
class BookControllerTest {

    @Autowired // DI: creates an instance of this class automatically
    private MockMvc mockMvc; // Simulates sending HTTP requests

    // Inject ObjectMapper (Jackson) to convert Objects -> JSON string automatically
    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private BookService bookService; // Mock the service

    @Test
    void createBook_ShouldReturn201_WhenRequestIsValid() throws Exception {

        // Arrange
        BookRequest request = new BookRequest(
            "Title",
            "Synopsis",
            "Author"
        );
        Book bookFromDb = Book.builder()
            .id(UUID.randomUUID())
            .title("Title")
            .build();

        when(bookService.createBook(any(BookRequest.class))).thenReturn(bookFromDb);

        // Act & assert
        mockMvc.perform(
                post("/v1/books")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)) // Convert object to JSON string programmatically
            )
            .andExpect(status().isCreated())            // Expect 201
            .andExpect(jsonPath("$.id").exists());      // Check JSON response
    }

}
