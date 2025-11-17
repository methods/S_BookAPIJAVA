package com.codesungrape.hmcts.bookapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO representing the required input for creating or replacing a Book resource.
 * This record mirrors the OpenAPI 'BookInput' schema.
 *As a Java record:
 * - all fields are implicitly final
 * - a canonical constructor is generated automatically
 * - accessor methods (e.g., title()) are generated
 * - equals, hashCode, and toString are automatically implemented
 *`@NotBlank` ensures that each field is required; missing or empty values result in
 * a 400 Bad Request response in Spring.
 * `@JsonProperty` maps snake_case JSON to camelCase Java properties using Jackson.
 */
public record BookRequest(
    @JsonProperty("title") @NotBlank(message = "Title is required") String title,
    @JsonProperty("synopsis") @NotBlank(message = "Synopsis is required") String synopsis,
    @JsonProperty("author") @NotBlank(message = "Author is required") String author) {
}
