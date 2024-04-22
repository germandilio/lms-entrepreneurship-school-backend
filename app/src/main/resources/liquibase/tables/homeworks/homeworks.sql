-- formatted liquibase sql

-- changeset germandiliio:create_homeworks_table
create table homeworks
(
    id            uuid primary key default gen_random_uuid() not null,
    lesson_id     uuid                                       not null,
    title         varchar(256) unique                        not null,
    publish_date  timestamp                                  not null,
    deadline_date timestamp,
    is_group      boolean                                    not null,
    payload       bytea                                      not null
);
