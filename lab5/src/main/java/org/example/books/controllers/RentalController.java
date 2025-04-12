package org.example.books.controllers;

import lombok.RequiredArgsConstructor;
import org.example.books.models.Rental;
import org.example.books.services.RentalService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    @PostMapping("/return/{id}")
    public Rental returnBook(@PathVariable Long id) {
        return rentalService.returnBook(id);
    }

    @GetMapping
    public Page<Rental> getAllRentals(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return rentalService.getAllRentals(pageable);
    }

    @GetMapping("/active")
    public Page<Rental> getActiveRentals(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return rentalService.getActiveRentals(pageable);
    }

    @GetMapping("/{id}")
    public Rental getRentalById(@PathVariable Long id) {
        return rentalService.getRentalById(id);
    }
}