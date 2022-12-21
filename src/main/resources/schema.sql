DROP ALL OBJECTS;
create table IF NOT EXISTS USERS
(
    USER_ID  INTEGER auto_increment,
    EMAIL    CHARACTER VARYING(50) not null,
    LOGIN    CHARACTER VARYING(50) not null,
    NAME     CHARACTER VARYING(50),
    BIRTHDAY DATE                  not null,
    constraint USERS_PK
        primary key (USER_ID)
);

create table IF NOT EXISTS FRIENDS
(
    USER_ID   INTEGER not null,
    FRIEND_ID INTEGER not null,
    constraint FRIENDS_USERS_USER_ID_FK
        foreign key (USER_ID) references USERS
            ON DELETE CASCADE,
    constraint FRIENDS_USERS_USER_ID_FK_2
        foreign key (FRIEND_ID) references USERS
            ON DELETE CASCADE
);

create table IF NOT EXISTS MPA
(
    MPA_ID INTEGER auto_increment,
    NAME   CHARACTER VARYING(50) not null,
    constraint "MPA_pk"
        primary key (MPA_ID)
);

create table IF NOT EXISTS FILMS
(
    FILM_ID      INTEGER auto_increment,
    NAME         CHARACTER VARYING(200) not null,
    DESCRIPTION  CHARACTER VARYING(200) not null,
    RELEASE_DATE DATE                   not null,
    DURATION     INTEGER                not null,
    MPA_ID       INTEGER                not null,
    constraint FILMS_PK
        primary key (FILM_ID),
    constraint FILMS_MPA_MPA_ID_FK
        foreign key (MPA_ID) references MPA
            ON DELETE CASCADE
);

create table IF NOT EXISTS FILM_LIKES
(
    FILM_ID INTEGER not null,
    USER_ID INTEGER not null,
    constraint FILMLIKES_FILMS_FILM_ID_FK
        foreign key (FILM_ID) references FILMS
            ON DELETE CASCADE,
    constraint FILMLIKES_USERS_USER_ID_FK
        foreign key (USER_ID) references USERS
            ON DELETE CASCADE
);

create table IF NOT EXISTS GENRE
(
    GENRE_ID INTEGER auto_increment,
    NAME     CHARACTER VARYING(50) not null,
    constraint "GENRE_pk"
        primary key (GENRE_ID)

);

create table IF NOT EXISTS FILM_GENRE
(
    FILM_ID  INTEGER not null,
    GENRE_ID INTEGER not null,
    constraint FILM_GENRE_FILMS_FILM_ID_FK
        foreign key (FILM_ID) references FILMS
            ON DELETE CASCADE,
    constraint FILM_GENRE_GENRE_GENRE_ID_FK
        foreign key (GENRE_ID) references GENRE
);