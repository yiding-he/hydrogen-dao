-- this is a comment line.

drop table if exists user;
create table user (
  id int not null, -- primary key
  username varchar(20) not null
);

insert into user (id, username) values (1, '--admin'); -- this is comment
