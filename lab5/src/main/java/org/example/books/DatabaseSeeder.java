package org.example.books;

import org.example.books.models.Author;
import org.example.books.models.Book;
import org.example.books.repositories.AuthorRepository;
import org.example.books.repositories.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class DatabaseSeeder {

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private BookRepository bookRepository;

    public void seedAuthors() {
        Author author1 = new Author();
        author1.setFirstName("John");
        author1.setLastName("Doe");
        author1.setBirthDate("1970-01-01");

        Author author2 = new Author();
        author2.setFirstName("Jane");
        author2.setLastName("Smith");
        author2.setBirthDate("1980-02-02");

        Author author3 = new Author();
        author3.setFirstName("Alice");
        author3.setLastName("Johnson");
        author3.setBirthDate("1990-03-03");

        authorRepository.saveAll(Arrays.asList(author1, author2, author3));
    }

    public void seedBooks() {
        Author author1 = authorRepository.findById(1L).orElse(null);
        Author author2 = authorRepository.findById(2L).orElse(null);
        Author author3 = authorRepository.findById(3L).orElse(null);

        if (author1 != null && author2 != null && author3 != null) {
            Book book1 = new Book();
            book1.setTitle("Book One");
            book1.setPages(100);
            book1.setIsAvailable(true);
            book1.setAuthor(author1);

            Book book2 = new Book();
            book2.setTitle("Book Two");
            book2.setPages(200);
            book2.setIsAvailable(false);
            book2.setAuthor(author2);

            Book book3 = new Book();
            book3.setTitle("Book Three");
            book3.setPages(150);
            book3.setIsAvailable(true);
            book3.setAuthor(author3);

            Book book4 = new Book();
            book4.setTitle("Book Four");
            book4.setPages(250);
            book4.setIsAvailable(true);
            book4.setAuthor(author1);

            Book book5 = new Book();
            book5.setTitle("Book Five");
            book5.setPages(300);
            book5.setIsAvailable(false);
            book5.setAuthor(author2);

            Book book6 = new Book();
            book6.setTitle("Book Six");
            book6.setPages(350);
            book6.setIsAvailable(true);
            book6.setAuthor(author3);

            bookRepository.saveAll(Arrays.asList(book1, book2, book3, book4, book5, book6));
        }
    }
}