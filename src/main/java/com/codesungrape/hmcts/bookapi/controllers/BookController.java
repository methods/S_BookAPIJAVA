package com.codesungrape.hmcts.bookapi.controllers;

import com.codesungrape.hmcts.bookapi.dto.BookRequest;
import com.codesungrape.hmcts.bookapi.entity.Book;
import com.codesungrape.hmcts.bookapi.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * `@RestController`: This tells Spring to register this class as a handler for incoming
 *  web requests and that the response bodies should be JSON.
 * `@RequestMapping("/v1/books")`: This sets the base URL for all methods in this class.
 * `@RequiredArgsConstructor`: This is the Lombok magic that generates a constructor for
 *  your final fields (Dependency Injection).
 * `@PostMapping`: Maps HTTP POST requests to this method.
 * `@Valid`: Crucial. This triggers the @NotBlank annotations you put in your BookRequest
 *  DTO earlier. Without this, invalid data would sneak into your service!
 * `@RequestBody`: Tells Spring to take the JSON from the request and map it into your
 *  Java object.
 *  ResponseEntity: We use this wrapper so we can explicitly set the status code to 201
 *  Created (instead of the default 200 OK).
 */

@RestController
@RequestMapping("/v1/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @PostMapping
    public ResponseEntity<Book> createBook(@Valid @RequestBody BookRequest request) {

        // 1. Call the business logic
        Book savedBook = bookService.createBook(request);

        // 2. Return the 201 Created status + the object
        return ResponseEntity.status(HttpStatus.CREATED).body(savedBook);

    }

}
