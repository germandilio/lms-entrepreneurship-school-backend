-- formatted liquibase sql

-- changeset germandiliio:create_tests_table
create table tests
(
    id            uuid primary key default gen_random_uuid() not null,
    lesson_id     uuid                                       not null,
    title         varchar(256) unique                        not null,
    publish_date  timestamp                                  not null,
    deadline_date timestamp,
    payload       bytea                                      not null
);
