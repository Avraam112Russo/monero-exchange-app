--liquibase formatted sql

--changeset felix:1\
create table t_bot_users(
    id bigserial primary key ,
    user_name varchar(64) not null ,
    created_at TIMESTAMP not null
);