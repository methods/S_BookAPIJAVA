package com.codesungrape.hmcts.bookapi.exception;

/**
 * Custom exception to signal that a requested resource (Book, Reservation, etc.)
 * could not be found, typically mapping to HTTP 404.
 */

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
