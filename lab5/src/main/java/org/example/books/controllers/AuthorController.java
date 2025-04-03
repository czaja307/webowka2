package org.example.books.controllers;

import lombok.RequiredArgsConstructor;
import org.example.books.models.Author;
import org.example.books.services.AuthorService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/authors")
@RequiredArgsConstructor
public class AuthorController {
    private final AuthorService authorService;

    @PostMapping
    public Author createAuthor(@RequestBody Author author) {
        return authorService.create(author);
    }

    @GetMapping("/{id}")
    public Optional<Author> getAuthor(@PathVariable long id) {
        return authorService.getById(id);
    }

    @GetMapping
    public List<Author> getAllAuthors() {
        return authorService.getAll();
    }

    @PutMapping("/{id}")
    public Optional<Author> updateAuthor(@PathVariable long id, @RequestBody Author author) {
        return authorService.update(id, author);
    }

    @DeleteMapping("/{id}")
    public boolean deleteAuthor(@PathVariable long id) {
        return authorService.delete(id);
    }
}