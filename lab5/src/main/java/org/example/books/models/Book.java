package org.example.books.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    private String title;

    private Integer pages;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Boolean isAvailable;

    @Transient
    private Long authorId;

    @ManyToOne
    @JoinColumn(name="author_id")
    private Author author;
}
