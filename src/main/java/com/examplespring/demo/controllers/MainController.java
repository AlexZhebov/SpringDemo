package com.examplespring.demo.controllers;

import com.examplespring.demo.models.persons;
import com.examplespring.demo.repo.PersonsRepositiry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
        import org.springframework.ui.Model;
        import org.springframework.web.bind.annotation.GetMapping;
        import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MainController {

    @Autowired
    private PersonsRepositiry personsRepositiry;

    @GetMapping("/")
    public String index(@RequestParam(name="name", required=false, defaultValue="World") String name, Model model) {
        model.addAttribute("title", "Главная страница");

        Iterable<persons> persons = personsRepositiry.findAll();
        model.addAttribute("persons", persons);
        return "index";
    }

}