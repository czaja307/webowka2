package org.example.books.services;

import lombok.RequiredArgsConstructor;
import org.example.books.exceptions.APIException;
import org.example.books.models.Reader;
import org.example.books.repositories.ReaderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReaderService {

    @Autowired
    private final ReaderRepository readerRepo;

    public Reader createReader(Reader reader) {
        reader.setId(null);
        return readerRepo.save(reader);
    }

    public Reader getReaderById(Long id) {
        return readerRepo.findById(id)
                .orElseThrow(() -> new APIException("Reader not found with id: " + id, HttpStatus.NOT_FOUND));
    }

    public Page<Reader> getAllReaders(Pageable pageable) {
        return readerRepo.findAll(pageable);
    }

    public Reader updateReader(Long id, Reader reader) {
        Optional<Reader> existingReader = readerRepo.findById(id);
        if (existingReader.isEmpty()) {
            throw new APIException("Reader not found with id: " + id, HttpStatus.NOT_FOUND);
        }
        Reader updatedReader = existingReader.get();
        updatedReader.setFirstName(reader.getFirstName());
        updatedReader.setLastName(reader.getLastName());
        return readerRepo.save(updatedReader);
    }

    public void deleteReader(Long id) {
        Optional<Reader> existingReader = readerRepo.findById(id);
        if (existingReader.isEmpty()) {
            throw new APIException("Reader not found with id: " + id, HttpStatus.NOT_FOUND);
        }
        if (!existingReader.get().getRentals().isEmpty()) {
            throw new APIException("Reader has active rentals and cannot be deleted.", HttpStatus.BAD_REQUEST);
        }
        readerRepo.deleteById(id);
    }
}