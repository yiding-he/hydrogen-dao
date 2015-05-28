CREATE TABLE USERS (
  ID            INT IDENTITY(1,1),
  USERNAME      VARCHAR(40),
  PASSWORD      VARCHAR(40),
  ROLE_ID       INT,
  REGISTER_TIME DATE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

---------------------------------

CREATE TABLE ROLES (
  ID   INT IDENTITY(1,1),
  NAME VARCHAR(40)
);

---------------------------------

CREATE TABLE LOBTEST (
  ID           INT IDENTITY(1,1),
  BLOB_CONTENT VARBINARY(MAX),
  CLOB_CONTENT VARCHAR(MAX)
);

---------------------------------

CREATE PROCEDURE add_one
  @value INT,
  @result INT OUTPUT
AS
SELECT @result = @value + 1