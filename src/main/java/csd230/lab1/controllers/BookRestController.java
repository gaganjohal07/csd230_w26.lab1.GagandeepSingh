
package csd230.lab1.controllers;

import csd230.lab1.entities.BookEntity;
import csd230.lab1.exceptions.ResourceNotFoundException;
import csd230.lab1.repositories.BookEntityRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rest/books")
@Tag(name = "Books", description = "REST API for managing books")
public class BookRestController {

    private final BookEntityRepository bookRepository;

    public BookRestController(BookEntityRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @GetMapping
    @Operation(summary = "Get all books")
    @ApiResponse(responseCode = "200", description = "Books retrieved successfully")
    public List<BookEntity> getAllBooks() {
        return bookRepository.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get book by ID")
    @ApiResponse(responseCode = "200", description = "Book found")
    @ApiResponse(responseCode = "404", description = "Book not found")
    public BookEntity getBookById(@PathVariable Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Book not found with ID: " + id));
    }

    @PostMapping
    @Operation(summary = "Create a new book")
    @ApiResponse(responseCode = "201", description = "Book created successfully")
    public ResponseEntity<BookEntity> createBook(@RequestBody BookEntity book) {
        book.setId(null);
        BookEntity savedBook = bookRepository.save(book);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedBook);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing book")
    @ApiResponse(responseCode = "200", description = "Book updated successfully")
    @ApiResponse(responseCode = "404", description = "Book not found")
    public BookEntity updateBook(
            @PathVariable Long id,
            @RequestBody BookEntity updatedBook) {

        BookEntity book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Book not found with ID: " + id));

        book.setTitle(updatedBook.getTitle());
        book.setPrice(updatedBook.getPrice());
        book.setCopies(updatedBook.getCopies());
        book.setAuthor(updatedBook.getAuthor());
        book.setIsbn(updatedBook.getIsbn());

        return bookRepository.save(book);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a book")
    @ApiResponse(responseCode = "204", description = "Book deleted successfully")
    @ApiResponse(responseCode = "404", description = "Book not found")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {

        BookEntity book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Book not found with ID: " + id));

        bookRepository.delete(book);
        return ResponseEntity.noContent().build();
    }
}
