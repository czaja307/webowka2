package org.example.books.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Book {
    private long id;
    private String title;
    private Author author;
    private int pages;
    private boolean isAvailable;



}
