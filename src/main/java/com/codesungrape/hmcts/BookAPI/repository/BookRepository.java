package com.codesungrape.hmcts.BookAPI.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.codesungrape.hmcts.BookAPI.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for Book Entity.
 * Spring Data JPA automatically provides CRUD operations based on the Entity and ID type.
 */
@Repository
public interface BookRepository extends JpaRepository<Book, UUID> { // same error msg here: Missing package statement: 'com.codesungrape.hmcts.BookAPI.repository'

    // Custom query to find books that have NOT been soft-deleted
    List<Book> findAllByDeletedFalse();

    // Custom query to find a specific, non-deleted book by ID.
    Optional<Book> findByIdAndDeleteFalse(UUID id);

}