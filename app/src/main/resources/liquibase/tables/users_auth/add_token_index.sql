-- formatted liquibase sql

-- changeset germandilio:add_pwd_reset_token_index
create index users_auth_password_reset_token_index on users_auth (password_reset_token);
