package com.codesungrape.hmcts.bookapi.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents the Book table in PostgreSQL as a JPA entity. Stores the persisted state of a Book
 * resource. HMCTS Rule: IDs must be opaque; using UUID for distributed ID generation. @Entity:
 * Marks the class as a JPA entity for Hibernate table mapping. @Table: Specifies the database
 * table; HMCTS: lowercase, singular table name. Lombok Annotations: @Getter: Generates getters for
 * all fields. @Setter: Generates setters for all fields. @NoArgsConstructor: Protected no-arg
 * constructor for JPA instantiation. @AllArgsConstructor: Constructor with all fields for test
 * convenience. @Builder: Adds builder pattern for easy object creation. Example: Book.builder()
 * .title("A") .author("B") .build();
 */
@Entity
@Table(name = "book")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // For JPA/Hibernate requirements
@AllArgsConstructor // For easy construction in tests
@Builder // For convenience in creating instances
public class Book {

    @Id // Primary key of the table
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "synopsis", nullable = false, columnDefinition = "TEXT")
    private String synopsis;

    @Column(name = "author", nullable = false)
    private String author;

    // Soft delete - makes DELETE operations idempotent (safe to repeat)
    // Using @Builder.Default to ensure the builder respects this initialization
    // if the field is not set explicitly.
    @Column(name = "deleted", nullable = false)
    @Builder.Default
    private boolean deleted = false;

    // `createdAt` is null upon object creation.
    // It will be set by the `onCreate()` method right before persistence.
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "modified_at")
    private Instant modifiedAt;

    // --- JPA lifecycle callbacks ---

    /**
     * Sets createdAt before persisting a new Book record.
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
    }

    // --- Business Logic Helper ---
    // HMCTS requires business logic to live in services; setter hooks are allowed.
    // Lifecycle callback: runs automatically before Hibernate updates a database record.

    /**
     * Updates modifiedAt before updating an existing Book record.
     */
    @PreUpdate
    protected void onUpdate() {
        this.modifiedAt = Instant.now();
    }
}
