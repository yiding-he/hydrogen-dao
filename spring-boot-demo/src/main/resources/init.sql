drop table if exists book;
create table book
(
    id          int primary key,
    name        varchar(100),
    create_time datetime
);