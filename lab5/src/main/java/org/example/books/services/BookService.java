package org.example.books.services;

import lombok.RequiredArgsConstructor;
import org.example.books.models.Book;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookService implements ICRUDService<Book> {
    private final List<Book> books = new ArrayList<>();


    @Override
    public Book create(Book book) {
        books.add(book);
        return book;
    }

    @Override
    public Optional<Book> getById(Long id) {
        return books.stream().filter(book -> Objects.equals(book.getId(), id)).findFirst();
    }

    @Override
    public List<Book> getAll() {
        return books;
    }

    @Override
    public Optional<Book> update(Long id, Book newBook) {
        return getById(id).map(book -> {
            book.setTitle(newBook.getTitle());
            book.setAuthor(newBook.getAuthor());
            return book;
        });
    }

    @Override
    public Boolean delete(Long id) {
        return books.removeIf(book -> Objects.equals(book.getId(), id));
    }
}
