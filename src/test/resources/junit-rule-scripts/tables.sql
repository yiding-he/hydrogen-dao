CREATE TABLE blog (
  id INT PRIMARY KEY ,
  title VARCHAR(100),
  last_update TIMESTAMP NOT NULL DEFAULT current_timestamp(),
  content TEXT,
  hidden varchar (5) default 'false' not null
);