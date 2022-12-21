package ru.yandex.practicum.filmorate.storage.feed;

import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.Operation;

import java.math.BigInteger;
import java.util.Collection;

public interface FeedStorage {

    Collection<Feed> getFeedByUserId(Integer id);

    void addFeed(Integer entityId, Integer userId, Integer timeStamp, EventType eventType, Operation operation);
}
