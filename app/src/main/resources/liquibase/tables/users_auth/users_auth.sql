-- formatted liquibase sql

-- changeset germandiliio:create_users_auth_table
create table "users_auth"
(
    id                   serial primary key,
    user_id              uuid                not null,
    login                varchar(256) unique not null,
    password             varchar(70),
    password_reset_token uuid,
    role                 varchar(16)         not null,
    is_deleted           boolean default false
);

INSERT INTO users_auth (user_id, login, password, role) VALUES ('bb7a1698-f64c-4ded-b544-3a9803d74ac1', 'admin', '$2a$10$5Mp6R8wN5J8t/TF9iZEEZeBb9wfpCG/tOYKk7UyYPVbHhKAcymac2', 'ADMIN') ON CONFLICT DO NOTHING;
