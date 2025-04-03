package org.example.books.controllers;

import lombok.RequiredArgsConstructor;
import org.example.books.models.Book;
import org.example.books.services.BookService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

// TODO: what the fuck is the autowired!??!??!

@RestController
@RequestMapping("/api/v1/books")
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;

    @PostMapping
    public Book createBook(@RequestBody Book book) {
        return bookService.create(book);
    }

    @GetMapping("/{id}")
    public Optional<Book> getBook(@PathVariable long id) {
        return bookService.getById(id);
    }

    @GetMapping
    public List<Book> getAllBooks() {
        return bookService.getAll();
    }

    @PutMapping("/{id}")
    public Optional<Book> updateBook(@PathVariable long id, @RequestBody Book book) {
        return bookService.update(id, book);
    }

    @DeleteMapping("/{id}")
    public boolean deleteBook(@PathVariable long id) {
        return bookService.delete(id);
    }
}
