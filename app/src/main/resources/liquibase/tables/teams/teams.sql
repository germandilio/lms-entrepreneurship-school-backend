-- formatted liquibase sql

-- changeset germandiliio:create_teams_table
create table "teams"
(
    id            uuid primary key default gen_random_uuid() not null,
    number        integer                                    not null,
    project_theme varchar(128),
    description   varchar(256),
    is_deleted    boolean          default false             not null
);
