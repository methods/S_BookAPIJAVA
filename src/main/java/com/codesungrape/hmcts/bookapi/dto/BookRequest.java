package com.codesungrape.hmcts.bookapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO representing the required input for creating or replacing a Book resource. This class mirrors
 * the OpenAPI 'BookInput' schema. @Value: Makes fields final, adds constructor, getters, equals,
 * hashCode, toString. @NotBlank: Field required; empty/missing value returns 400 Bad Request in
 * Spring. @JsonProperty: Maps snake_case JSON to camelCase Java using Jackson.
 */
public record BookRequest(
    @JsonProperty("title") @NotBlank(message = "Title is required") String title,
    @JsonProperty("synopsis") @NotBlank(message = "Synopsis is required") String synopsis,
    @JsonProperty("author") @NotBlank(message = "Author is required") String author) {
}
