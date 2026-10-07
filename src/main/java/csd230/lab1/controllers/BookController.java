package csd230.lab1.controllers;

import csd230.lab1.entities.BookEntity;
import csd230.lab1.repositories.BookEntityRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class BookController {

    private final BookEntityRepository bookRepository;

    public BookController(BookEntityRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @GetMapping("/books")
    public String showBooks(Model model) {
        List<BookEntity> books = bookRepository.findAll();

        model.addAttribute("books", books);

        return "bookList";
    }
}