package org.example.books.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/api/v1", "/api/v1/"})
public class HealthcheckController {

    @GetMapping({"/health", "/health/", "/healthcheck", "/healthcheck/"})
    public String health() {
        return "Elo żelo!";
    }
}
