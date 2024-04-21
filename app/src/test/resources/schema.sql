drop table if exists users;
drop table if exists teams;
drop table if exists users_groups;
drop table if exists users_auth;
drop table if exists roles;

create table "users"
(
    id                uuid primary key default gen_random_uuid() not null,
    name              varchar(128)                               not null,
    surname           varchar(128)                               not null,
    patronymic        varchar(128),
    messenger_contact varchar(256),
    sex               varchar(10)                                not null,
    email             varchar(256)                               not null,
    phone_number      varchar(16),
    group_id          integer references teams (id),
    role              varchar(16)                                not null,
    balance           decimal(13, 4)   default 0                 not null,
    is_deleted        boolean          default false             not null
);

create table "teams"
(
    id          serial primary key,
    number      integer               not null,
    title       varchar(128),
    description varchar(256),
    is_deleted  boolean default false not null
);

create table "users_auth"
(
    user_id    uuid primary key    not null,
    login      varchar(256) unique not null,
    password   varchar(70),
    token      uuid                not null,
    role       varchar(16)         not null,
    is_deleted boolean default false
);