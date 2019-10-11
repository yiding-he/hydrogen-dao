drop table if exists book;
create table book
(
    id          int primary key,
    name        varchar(100),
    create_time datetime
);

insert into book set id=1, name='第一本书', create_time=current_timestamp();
insert into book set id=2, name='第二本书', create_time=current_timestamp();
insert into book set id=3, name='第三本书', create_time=current_timestamp();
