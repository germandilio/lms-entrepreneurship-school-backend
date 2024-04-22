-- formatted liquibase sql

-- changeset germandiliio:create_lessons_table
create table lessons
(
    id            uuid primary key default gen_random_uuid() not null,
    lesson_number integer unique                             not null,
    title         varchar(256)                               not null,
    publish_date  timestamp                                  not null,
    payload       bytea                                      not null
);
