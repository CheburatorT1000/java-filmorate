package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class MpaService {

    private final MpaStorage mpaStorage;

    public Collection<MPA> getAllMPA() {
        return mpaStorage.findAll();
    }

    public MPA getMPAById(int id) {
        return mpaStorage.findMpaById(id)
                .orElseThrow(() -> new NotFoundException("Рейтинг с таким id отсутствует"));
    }
}
