package org.example.books.services;

import lombok.RequiredArgsConstructor;
import org.example.books.exceptions.APIException;
import org.example.books.models.Author;
import org.example.books.repositories.AuthorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
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

    public Author getById(Long id) {
        return authorRepo.findById(id)
                .orElseThrow(() -> new APIException("Author not found with id: " + id, HttpStatus.NOT_FOUND));
    }

    public Page<Author> getAll(Pageable pageable) {
        return authorRepo.findAll(pageable);
    }

    public Author update(Long id, Author author) {
        Optional<Author> existingAuthor = authorRepo.findById(id);
        if (existingAuthor.isEmpty()) {
            throw new APIException("Author not found with id: " + id, HttpStatus.NOT_FOUND);
        }
        Author updatedAuthor = existingAuthor.get();
        updatedAuthor.setFirstName(author.getFirstName());
        updatedAuthor.setLastName(author.getLastName());
        updatedAuthor.setBirthDate(author.getBirthDate());
        return authorRepo.save(updatedAuthor);
    }

    public void delete(Long id) {
        Optional<Author> existingAuthor = authorRepo.findById(id);
        if (existingAuthor.isEmpty()) {
            throw new APIException("Author not found with id: " + id, HttpStatus.NOT_FOUND);
        }
        if (!existingAuthor.get().getBooks().isEmpty()) {
            throw new APIException("Cannot delete author with assigned books.", HttpStatus.BAD_REQUEST);
        }
        authorRepo.deleteById(id);
    }
}
