package ru.yandex.practicum.filmorate.model;

import lombok.*;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.Operation;

import java.math.BigInteger;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Feed {

   private Integer id;
    private Integer entityId;
    private Integer userId;
    private Integer timeStamp;
    private EventType eventType;
    private Operation operation;
  }
