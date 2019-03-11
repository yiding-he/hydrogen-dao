CREATE TABLE blog (
  id INT PRIMARY KEY ,
  title VARCHAR(100),
  create_time TIMESTAMP NOT NULL DEFAULT current_timestamp(),
  last_update TIMESTAMP NOT NULL DEFAULT current_timestamp(),
  content TEXT,
  hidden varchar (5) default 'false' not null
);

create table blog2 (
  id INT PRIMARY KEY ,
  title VARCHAR(100),
  createTime TIMESTAMP NOT NULL DEFAULT current_timestamp(),
  content TEXT,
  hidden varchar (5) default 'false' not null
);

insert into blog2 set id=1, title='title', content='content';