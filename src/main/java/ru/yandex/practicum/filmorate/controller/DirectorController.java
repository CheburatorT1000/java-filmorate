package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequestMapping("/directors")
public class DirectorController {

    @Autowired
    private final DirectorService directorService;

    public DirectorController(DirectorService directorService) {
        this.directorService = directorService;
    }

    @GetMapping
    public Collection<Director> findAll() {
        return directorService.findAll();
    }

    @PostMapping
    public Director create(@Valid @RequestBody Director user) {
        return directorService.create(user);
    }

    @PutMapping
    public Director put(@Valid @RequestBody Director user) {
        return directorService.update(user);
    }

    @GetMapping("{id}")
    public Director getById(@PathVariable int id) {
        return directorService.findDirectorById(id);
    }

    @DeleteMapping("{id}")
    public void deleteDirectorById(@PathVariable int id) {
        directorService.deleteDirectorByID(id);
    }
}