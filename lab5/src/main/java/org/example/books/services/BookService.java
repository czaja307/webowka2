package org.example.books.services;

import lombok.RequiredArgsConstructor;
import org.example.books.dtos.BookRequestDTO;
import org.example.books.exceptions.APIException;
import org.example.books.models.Author;
import org.example.books.models.Book;
import org.example.books.repositories.AuthorRepository;
import org.example.books.repositories.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookService {
    @Autowired
    BookRepository bookRepo;

    @Autowired
    AuthorRepository authorRepo;

    public Book create(BookRequestDTO bookRequestDTO) {
        Book book = new Book();
        book.setTitle(bookRequestDTO.getTitle());
        book.setPages(bookRequestDTO.getPages());
        book.setIsAvailable(bookRequestDTO.getIsAvailable());

        if (bookRequestDTO.getAuthorId() != null) {
            Optional<Author> author = authorRepo.findById(bookRequestDTO.getAuthorId());
            author.ifPresent(book::setAuthor);
        }

        return bookRepo.save(book);
    }

    public Book update(Long id, BookRequestDTO bookRequestDTO) {
        Optional<Book> existingBook = bookRepo.findById(id);
        if (existingBook.isEmpty()) {
            throw new APIException("Book not found with id: " + id, HttpStatus.NOT_FOUND);
        }
        Book book = existingBook.get();
        book.setTitle(bookRequestDTO.getTitle());
        book.setPages(bookRequestDTO.getPages());
        book.setIsAvailable(bookRequestDTO.getIsAvailable());

        if (bookRequestDTO.getAuthorId() != null) {
            Optional<Author> author = authorRepo.findById(bookRequestDTO.getAuthorId());
            author.ifPresent(book::setAuthor);
        }

        return bookRepo.save(book);
    }

    public Book getById(Long id) {
        return bookRepo.findById(id).orElseThrow(() -> new APIException("Book not found with id: " + id, HttpStatus.NOT_FOUND));
    }

    public List<Book> getAll() {
        return bookRepo.findAll();
    }

    public void delete(Long id) {
        if (!bookRepo.existsById(id)) {
            throw new APIException("Book not found with id: " + id, HttpStatus.NOT_FOUND);
        }
        bookRepo.deleteById(id);
    }
}