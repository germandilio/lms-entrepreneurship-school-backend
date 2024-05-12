-- formatted liquibase sql

-- changeset germandilio:create_grades_table
create table grades
(
    id            uuid primary key default gen_random_uuid() not null,
    owner_id      uuid                                       not null,
    task_id       uuid                                       not null,
    task_type     varchar(15)                                not null,
    submission_id uuid,
    admin_grade   int,
    admin_comment varchar(1500)
);

-- changeset germandilio:create_grades_indexes
create index grades_owner_id_idx on grades (owner_id);
create index grades_task_id_idx on grades (task_id);
create index grades_submission_id_idx on grades (submission_id);