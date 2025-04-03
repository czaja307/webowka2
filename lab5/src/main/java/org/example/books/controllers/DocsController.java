package org.example.books.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DocsController {

    @GetMapping({"/", "/docs", "/docs/", "api/v1", "/api/v1/", "api/v1/docs", "/api/v1/docs", "/api/v1/docs/"})
    public String redirectToDocs() {
        return "redirect:/swagger-ui/index.html";
    }
}
