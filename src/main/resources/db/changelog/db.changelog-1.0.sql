--liquibase formatted sql

--changeset felix:1\
create table t_users_monero_app(
    id int primary key ,
    name varchar(64)
)