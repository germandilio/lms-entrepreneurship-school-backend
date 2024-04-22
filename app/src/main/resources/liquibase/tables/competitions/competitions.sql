-- formatted liquibase sql

-- changeset germandiliio:create_competitions_table
create table competitions
(
    id            uuid primary key default gen_random_uuid() not null,
    title         varchar(256) unique                        not null,
    publish_date  timestamp                                  not null,
    deadline_date timestamp,
    payload       bytea                                      not null
);
