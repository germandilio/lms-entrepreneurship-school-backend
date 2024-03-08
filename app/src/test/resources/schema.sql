drop table if exists users;
drop table if exists groups;
drop table if exists users_groups;
drop table if exists users_auth;
drop table if exists roles;
drop table if exists users_authorities;
drop table if exists authorities;

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
    balance           decimal(13, 4)   default 0                 not null,
    is_deleted        boolean          default false             not null
);

create table "groups"
(
    id          serial primary key,
    number      integer not null,
    title       varchar(128),
    description varchar(256)
);

create table "users_groups"
(
    user_id  uuid references users (id),
    group_id integer references groups (id),
    primary key (user_id, group_id)
);

create table "roles"
(
    id   serial primary key,
    role varchar(64) not null
);

create table "users_auth"
(
    user_id    uuid primary key    not null,
    login      varchar(256) unique not null,
    password   varchar(70),
    role_id    integer references roles (id),
    is_deleted boolean default false
);
