
create table FEED
(
EVENT_ID   INTEGER auto_increment,
ENTITY_ID  INTEGER               not null,
USER_ID    INTEGER               not null,
TIME_STAMP INTEGER               not null,
EVENT_TYPE CHARACTER VARYING(10) not null,
OPERATION  CHARACTER VARYING(10) not null,
constraint "FEED_pk"
primary key (EVENT_ID),
constraint FEED_USERS_USER_ID_FK
foreign key (USER_ID) references USERS
);
 "Title")