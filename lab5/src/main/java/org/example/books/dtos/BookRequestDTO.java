package org.example.books.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookRequestDTO {
    private String title;
    private Integer pages;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Boolean isAvailable;
    private Long authorId;
}