CREATE TABLE blog (
  id INT PRIMARY KEY ,
  title VARCHAR(100),
  create_time TIMESTAMP NOT NULL DEFAULT current_timestamp(),
  content TEXT
);