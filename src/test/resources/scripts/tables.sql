CREATE TABLE blog (
  id INT PRIMARY KEY ,
  title VARCHAR(100),
  create_time TIMESTAMP DEFAULT current_timestamp(),
  content TEXT
);