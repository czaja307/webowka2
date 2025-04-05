package org.example.books.services;

import lombok.RequiredArgsConstructor;
import org.example.books.models.Author;
import org.example.books.repositories.AuthorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthorService {
    @Autowired
    AuthorRepository authorRepo;

    public Author create(Author author) {
        author.setId(null);
        return authorRepo.save(author);
    }

    public Optional<Author> getById(Long id) {
        return authorRepo.findById(id);
    }

    public List<Author> getAll() {
        return authorRepo.findAll();
    }

    public Author update(Long id, Author author) {
        Optional<Author> existingAuthor = authorRepo.findById(id);
        if (existingAuthor.isPresent()) {
            Author updatedAuthor = existingAuthor.get();
            updatedAuthor.setFirstName(author.getFirstName());
            updatedAuthor.setLastName(author.getLastName());
            updatedAuthor.setBirthDate(author.getBirthDate());
            return authorRepo.save(updatedAuthor);
        }
        return null;
    }

    public void delete(Long id) {
        if (authorRepo.existsById(id) && !authorRepo.findById(id).get().getBooks().isEmpty()) {
            throw new IllegalStateException("Cannot delete author with assigned books.");
        }
        authorRepo.deleteById(id);
    }
}
