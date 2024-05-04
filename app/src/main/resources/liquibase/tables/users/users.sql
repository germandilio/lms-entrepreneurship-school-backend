-- formatted liquibase sql

-- changeset germandiliio:create_users_table
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
    role              varchar(16)                                not null,
    balance           decimal(13, 4)   default 0                 not null,
    is_deleted        boolean          default false             not null
);

INSERT INTO users (id, name, surname, sex, email, role) VALUES ('bb7a1698-f64c-4ded-b544-3a9803d74ac1', 'admin', 'admin', 'MALE', 'admin', 'ADMIN') ON CONFLICT DO NOTHING;
