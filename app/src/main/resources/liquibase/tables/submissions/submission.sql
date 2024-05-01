-- formatted liquibase sql

-- changeset germandilio:create_submissions_table
create table submissions
(
    id              uuid primary key,
    task_id         uuid      not null,
    owner_id        uuid      not null,
    publisher_id    uuid      not null,
    team_id        uuid,
    submission_date timestamp not null,
    payload         bytea     not null
);