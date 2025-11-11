package com.codesungrape.hmcts.BookAPI.entity;

import jakarta.persistence.*;
import java.util.UUID;
import lombok.*;

/**
 * JPA Entity representing the Book table in PostgreSQL.
 * This holds the persisted state of the resource.
 * HMCTS Rule Check: IDs must be opaque strings. Using UUID for distributed ID generation.
 * @Entity: Marks the class as a JPA entity - tells hibernate to map Java classes to database tables.
 * @Table: Defines which database table this entity maps to. HMCTS Naming: Lowercase, singular table name is common practice.
 * Lombok annotations:
 * @Getter: Automatically generates getters for all fields.
 * @Setter: Automatically generates setters.
 * @AllArgsConstructor: Generates a no-argument constructor (required by JPA).
 *                      JPA needs to instantiate the entity using reflection. 'PROTECTED' prevents misuse.
 * @Builder: Adds a builder pattern for clean object creation.
*            You can do Book.builder().title("A").author("B").build();
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
    @Column(name = "id", nullable = false) // maps the field to a database column named 'id' + 'nullable =false' database column cannot be NULL.
    private UUID id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "synopsis", nullable = false, columnDefinition = "TEXT")
    private String synopsis;

    @Column(name = "author", nullable = false)
    private String author;

    // Soft delete - makes DELETE operations idempotent (safe to repeat)
    @Column(name = "deleted", nullable = false)
    @Builder.Default
    private boolean deleted = false;

    @Column(name = "created_at", nullable = false)
    private java.time.Instant createdAt;

    @Column(name = "modified_at")
    private java.time.Instant modifiedAt;

    // --- Business Logic Helper ---
    // HMCTS mandates business logic in services, but a setter hook is acceptable.
    // Lifecycle callback - special method runs automatically before Hibernate persists a record in the database.
    @PrePersist
    protected void onCreate() {
        this.createdAt = java.time.Instant.now();
    }

    // Lifecycle callback - special method runs automatically before Hibernate updates a record in the database.
    @PreUpdate
    protected void onUpdate() {
        this.modifiedAt = java.time.Instant.now();
    }


}