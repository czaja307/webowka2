package org.example.books;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BooksApplication implements CommandLineRunner {

    @Autowired
    private final DatabaseSeeder databaseSeeder;

    public BooksApplication(DatabaseSeeder databaseSeeder) {
        this.databaseSeeder = databaseSeeder;
    }

    public static void main(String[] args) {
        SpringApplication.run(BooksApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        databaseSeeder.seedAuthors();
        databaseSeeder.seedBooks();
    }
}
