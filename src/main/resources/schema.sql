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
        foreign key (USER_ID) references USERS,
    constraint FRIENDS_USERS_USER_ID_FK_2
        foreign key (FRIEND_ID) references USERS
);
create table IF NOT EXISTS MPA_RATE
(
    RATE_ID INTEGER auto_increment,
    NAME    CHARACTER VARYING(50) not null,
    constraint "MPA_RATE_pk"
        primary key (RATE_ID)
);
create table IF NOT EXISTS FILMS
(
    FILM_ID      INTEGER auto_increment,
    NAME         CHARACTER VARYING(200) not null,
    DESCRIPTION  CHARACTER VARYING(200) not null,
    RELEASE_DATE DATE                   not null,
    DURATION     INTEGER                not null,
    RATE_ID      INTEGER                not null,
    constraint FILMS_PK
        primary key (FILM_ID),
    constraint FILMS_MPA_RATE_RATE_ID_FK
        foreign key (RATE_ID) references MPA_RATE
);
create table IF NOT EXISTS FILM_LIKES
(
    FILM_ID INTEGER not null,
    USER_ID INTEGER not null,
    constraint FILMLIKES_FILMS_FILM_ID_FK
        foreign key (FILM_ID) references FILMS,
    constraint FILMLIKES_USERS_USER_ID_FK
        foreign key (USER_ID) references USERS
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
        foreign key (FILM_ID) references FILMS,
    constraint FILM_GENRE_GENRE_GENRE_ID_FK
        foreign key (GENRE_ID) references GENRE
);