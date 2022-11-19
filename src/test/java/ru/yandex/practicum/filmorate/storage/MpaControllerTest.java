package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class MpaControllerTest {

    private final MpaStorage mpaStorage;

    @Test
    void getAllMPA() {
        Collection<MPA> mpas = mpaStorage.findAll();
        assertEquals(5, mpas.size());
    }

    @Test
    void getMPAById() {
        MPA mpa = mpaStorage.findMpaById(4).get();
        assertEquals("R", mpa.getName());
    }
}