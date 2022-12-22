package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;

import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class DirectorService {

    private final DirectorStorage directorStorage;

    public Director create(Director director) {
        Director directorFromCreator = directorCreator(director);
        log.info("Добавляем режиссера в коллекцию");
        return directorStorage.save(directorFromCreator);
    }

    public Director findDirectorById(int id) {
        return directorStorage.findDirectorById(id).
                orElseThrow(() -> new NotFoundException("Режиссер не найден!"));
    }

    public Director update(Director director) {
        Director directorFromCreator = directorCreator(director);
        log.info("Обновляем режиссера в коллекции");
        findDirectorById(directorFromCreator.getId());
        return directorStorage.update(directorFromCreator);
    }

    public Director directorCreator(Director director) {
        log.info("Создаем объект режиссер");
        Director directorFromBuilder = Director.builder()
                .id(director.getId())
                .name(director.getName())
                .build();
        log.info("Объект режиссер создан, имя : '{}'", directorFromBuilder.getName());
        return directorFromBuilder;
    }

    public Collection<Director> findAll() {
        log.info("Выводим список всех режиссеров");
        return directorStorage.findAll();
    }

    public void deleteDirectorByID (int id) {
        log.info(String.format("Удаляем режиссера с id: %s", id));
        directorStorage.deleteDirector(id);
    }

}
