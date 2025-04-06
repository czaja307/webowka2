package org.example.books.controllers;

import lombok.RequiredArgsConstructor;
import org.example.books.dtos.BookRequestDTO;
import org.example.books.models.Book;
import org.example.books.services.BookService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/books")
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;

    @PostMapping
    public Book createBook(@RequestBody BookRequestDTO bookRequestDTO) {
        return bookService.create(bookRequestDTO);
    }

    @GetMapping("/{id}")
    public Book getBook(@PathVariable Long id) {
        return bookService.getById(id);
    }

    @GetMapping
    public List<Book> getAllBooks() {
        return bookService.getAll();
    }

    @PutMapping("/{id}")
    public Book updateBook(@PathVariable Long id, @RequestBody BookRequestDTO bookRequestDTO) {
        return bookService.update(id, bookRequestDTO);
    }

    @DeleteMapping("/{id}")
    public void deleteBook(@PathVariable Long id) {
        bookService.delete(id);
    }
}