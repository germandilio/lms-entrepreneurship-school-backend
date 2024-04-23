-- formatted liquibase sql

-- changeset germandiliio:create_exams_table
create table exams
(
    id            uuid primary key default gen_random_uuid() not null,
    title         varchar(256) unique                        not null,
    publish_date  timestamp                                  not null,
    deadline_date timestamp,
    payload       bytea                                      not null
);
