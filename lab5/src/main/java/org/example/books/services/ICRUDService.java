package org.example.books.services;

import java.util.List;
import java.util.Optional;

public interface ICRUDService<T> {
    T create(T t);

    Optional<T> getById(Long id);

    List<T> getAll();

    Optional<T> update(Long id, T t);

    Boolean delete(Long id);
}
