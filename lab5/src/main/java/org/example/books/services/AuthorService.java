package org.example.books.services;

import lombok.RequiredArgsConstructor;
import org.example.books.models.Author;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthorService implements ICRUDService<Author> {
    private final List<Author> authors = new ArrayList<>();

    @Override
    public Author create(Author author) {
        authors.add(author);
        return author;
    }

    @Override
    public Optional<Author> getById(long id) {
        return authors.stream().filter(author -> author.getId() == id).findFirst();
    }

    @Override
    public List<Author> getAll() {
        return authors;
    }

    @Override
    public Optional<Author> update(long id, Author newAuthor) {
        return getById(id).map(author -> {
            author.setName(newAuthor.getName());
            author.setSurname(newAuthor.getSurname());
            author.setBirthDate(newAuthor.getBirthDate());
            return author;
        });
    }

    @Override
    public boolean delete(long id) {
        return authors.removeIf(author -> author.getId() == id);
    }
}
