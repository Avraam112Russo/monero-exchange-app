--liquibase formatted sql

--changeset felix:1
create table t_bot_users(
    id bigserial primary key ,
    user_name varchar(64) not null ,
    created_at TIMESTAMP not null ,
    chat_id bigint
);
create table t_xmr_order(
    id bigserial primary key ,
    address varchar(255)  ,
    xmr_quantity double precision not null ,
    price_in_ruble double precision not null,
    payment_method varchar(32) not null ,
    user_id bigint,
    foreign key (user_id) references t_bot_users(id)
)