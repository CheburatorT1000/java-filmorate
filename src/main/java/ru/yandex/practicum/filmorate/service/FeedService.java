package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.Operation;
import ru.yandex.practicum.filmorate.storage.feed.FeedStorage;

import java.time.Instant;
import java.util.Collection;

@Service
@Slf4j
@RequiredArgsConstructor
public class FeedService {
    private final FeedStorage feedStorage;

    public void addFeed(Integer entityId, Integer userId, EventType eventType, Operation operation) {
        long timeStamp = Instant.now().toEpochMilli();
        feedStorage.addFeed(entityId, userId, timeStamp, eventType, operation);
        log.info("Добавлено событие с id: '{}'", entityId);
    }

    public Collection<Feed> getFeedByUserId(Integer userId) {
        Collection<Feed> feed = feedStorage.getFeedByUserId(userId);
        log.info("Получен список событий, размер списка '{}'", feed.size());
        return feed;
    }
}
