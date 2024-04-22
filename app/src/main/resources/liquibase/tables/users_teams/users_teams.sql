-- formatted liquibase sql

-- changeset germandiliio:create_users_teams_table
create table users_teams
(
    user_id uuid not null,
    team_id uuid not null,
    primary key (user_id, team_id)
);