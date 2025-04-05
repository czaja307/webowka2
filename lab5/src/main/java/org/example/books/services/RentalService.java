package org.example.books.services;

import lombok.RequiredArgsConstructor;
import org.example.books.models.Book;
import org.example.books.models.Reader;
import org.example.books.models.Rental;
import org.example.books.repositories.BookRepository;
import org.example.books.repositories.ReaderRepository;
import org.example.books.repositories.RentalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RentalService {

    @Autowired
    private final RentalRepository rentalRepo;

    @Autowired
    private final BookRepository bookRepo;

    @Autowired
    private final ReaderRepository readerRepo;

    public Rental rentBook(Long bookId, Long readerId) {
        Optional<Book> book = bookRepo.findById(bookId);
        Optional<Reader> reader = readerRepo.findById(readerId);

        if (book.isPresent() && reader.isPresent() && book.get().getIsAvailable()) {
            Rental rental = new Rental();
            rental.setBook(book.get());
            rental.setReader(reader.get());
            rental.setRentalDate(new Date());
            book.get().setIsAvailable(false);
            bookRepo.save(book.get());
            return rentalRepo.save(rental);
        }
        throw new IllegalStateException("Book is not available or reader does not exist.");
    }

    public Rental returnBook(Long rentalId) {
        Optional<Rental> rental = rentalRepo.findById(rentalId);

        if (rental.isPresent()) {
            Rental existingRental = rental.get();
            existingRental.setReturnDate(new Date());
            Book book = existingRental.getBook();
            book.setIsAvailable(true);
            bookRepo.save(book);
            return rentalRepo.save(existingRental);
        }
        throw new IllegalStateException("Rental does not exist.");
    }

    public List<Rental> getAllRentals() {
        return rentalRepo.findAll();
    }

    public List<Rental> getActiveRentals() {
        return rentalRepo.findAll().stream().filter(rental -> rental.getReturnDate() == null).collect(Collectors.toList());
    }

    public Optional<Rental> getRentalById(Long id) {
        return rentalRepo.findById(id);
    }
}