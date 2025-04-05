package org.example.books.services;

import lombok.RequiredArgsConstructor;
import org.example.books.models.Reader;
import org.example.books.repositories.ReaderRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

    public Optional<Reader> getReaderById(Long id) {
        return readerRepo.findById(id);
    }

    public List<Reader> getAllReaders() {
        return readerRepo.findAll();
    }

    public Reader updateReader(Long id, Reader reader) {
        Optional<Reader> existingReader = readerRepo.findById(id);
        if (existingReader.isPresent()) {
            Reader updatedReader = existingReader.get();
            updatedReader.setFirstName(reader.getFirstName());
            updatedReader.setLastName(reader.getLastName());
            return readerRepo.save(updatedReader);
        }
        return null;
    }

    public void deleteReader(Long id) {
        if (readerRepo.existsById(id) && !readerRepo.findById(id).get().getRentals().isEmpty()) {
            throw new IllegalStateException("Cannot delete reader with active rentals.");
        }
        readerRepo.deleteById(id);
    }
}