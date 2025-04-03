package org.example.books.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Book {

    @Id
    @GeneratedValue
    private Long id;

    private String title;

    private Integer pages;

    private Boolean isAvailable;

    @ManyToOne
    @JoinColumn(name="author_id")
    private Author author;
}
