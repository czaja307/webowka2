package org.example.books.controllers;

import lombok.RequiredArgsConstructor;
import org.example.books.models.Reader;
import org.example.books.services.ReaderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/readers")
@RequiredArgsConstructor
public class ReaderController {

    private final ReaderService readerService;

    @PostMapping
    public Reader createReader(@RequestBody Reader reader) {
        return readerService.createReader(reader);
    }

    @GetMapping("/{id}")
    public Reader getReader(@PathVariable Long id) {
        return readerService.getReaderById(id);
    }

    @GetMapping
    public Page<Reader> getAllReaders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return readerService.getAllReaders(pageable);
    }

    @PutMapping("/{id}")
    public Reader updateReader(@PathVariable Long id, @RequestBody Reader reader) {
        return readerService.updateReader(id, reader);
    }

    @DeleteMapping("/{id}")
    public void deleteReader(@PathVariable Long id) {
        readerService.deleteReader(id);
    }
}