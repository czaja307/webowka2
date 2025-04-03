package org.example.books.services;

import java.util.List;
import java.util.Optional;

public interface ICRUDService<T> {
    T create(T t);

    Optional<T> getById(long id);

    List<T> getAll();

    Optional<T> update(long id, T t);

    boolean delete(long id);
}
