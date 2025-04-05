package org.example.books.controllers;

import lombok.RequiredArgsConstructor;
import org.example.books.models.Rental;
import org.example.books.services.RentalService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/rentals")
@RequiredArgsConstructor
public class RentalController {

    private final RentalService rentalService;

    @PostMapping("/rent")
    public Rental rentBook(@RequestParam Long bookId, @RequestParam Long readerId) {
        return rentalService.rentBook(bookId, readerId);
    }

    @PostMapping("/return")
    public Rental returnBook(@RequestParam Long rentalId) {
        return rentalService.returnBook(rentalId);
    }

    @GetMapping
    public List<Rental> getAllRentals() {
        return rentalService.getAllRentals();
    }

    @GetMapping("/active")
    public List<Rental> getActiveRentals() {
        return rentalService.getActiveRentals();
    }

    @GetMapping("/{id}")
    public Optional<Rental> getRentalById(@PathVariable Long id) {
        return rentalService.getRentalById(id);
    }
}