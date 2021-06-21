drop table if exists book;
create table book
(
    id          int primary key,
    name        varchar(100),
    create_time datetime
);

insert into book set id=1, name='First Book',  create_time=current_timestamp();
insert into book set id=2, name='Second Book', create_time=current_timestamp();
insert into book set id=3, name='Third Book',  create_time=current_timestamp();
