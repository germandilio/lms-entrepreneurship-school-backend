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

-- changeset germandilio:create_submissions_indexes
create index submissions_task_id_index on submissions (task_id);
create index submissions_owner_id_index on submissions (owner_id);