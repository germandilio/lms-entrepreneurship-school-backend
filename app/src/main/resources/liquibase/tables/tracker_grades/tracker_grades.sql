-- formatted liquibase sql

-- changeset germandilio:create_tracker_grades_table
create table tracker_grades
(
    tracker_id uuid not null,
    grade_id   uuid not null,
    grade      int,
    comment    varchar(1500),
    primary key (tracker_id, grade_id)
);

-- changeset germandilio:create_tracker_grades_indexes
create index tracker_grades_grade_id_idx on tracker_grades(grade_id);
