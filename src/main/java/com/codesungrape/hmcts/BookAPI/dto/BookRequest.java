package com.codesungrape.hmcts.BookAPI.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Value;

/**
 * DTO representing the required input for creating or replacing a Book resource.
 * This class mirrors the OpenAPI 'BookInput' schema.
 * @Value: Makes all fields 'final' (immutable), generates constructor, getters, and equals/hashCode/toString.
 * @NotBlank: Enforces the required status from your OpenAPI schema. If the field is missing or an empty string, Spring will return a 400 Bad Request.
 * @JsonProperty: Jackson library - maps snake_case JSON (HMCTS rules) to camelCase Java
 */
@Value
public class BookRequest {
    @NotBlank(message= "Title is required") // enforces
    @JsonProperty("title")
    String title;

    @NotBlank(message = "Synopsis is required")
    @JsonProperty("synopsis")
    String synopsis;

    @NotBlank(message = "Author is required")
    @JsonProperty("author")
    String author;
}