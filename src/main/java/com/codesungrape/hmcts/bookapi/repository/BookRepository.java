package com.codesungrape.hmcts.bookapi.repository;

import com.codesungrape.hmcts.bookapi.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for Book entities. Provides CRUD operations and custom queries for
 * non-deleted books. Spring Data JPA automatically implements this interface at runtime.
 */
@Repository
public interface BookRepository extends JpaRepository<Book, UUID> {

    /**
     * Custom query retrieves all Book records that have not been soft-deleted.
     */
    List<Book> findAllByDeletedFalse();

    /**
     * Retrieves a single Book by ID, if it exists and has not been soft-deleted.
     */
    Optional<Book> findByIdAndDeletedFalse(UUID id);
}
