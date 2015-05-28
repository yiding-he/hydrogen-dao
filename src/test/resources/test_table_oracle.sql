CREATE TABLE "USERS" (
  "ID"            NUMBER PRIMARY KEY,
  "USERNAME"      VARCHAR2(40),
  "PASSWORD"      VARCHAR2(40),
  "ROLE_ID"       NUMBER,
  "REGISTER_TIME" DATE DEFAULT SYSDATE NOT NULL
);

---------------------------------

CREATE TABLE "ROLES" (
  "ID"   NUMBER PRIMARY KEY,
  "NAME" VARCHAR2(40)
);

---------------------------------

CREATE TABLE LOBTEST (
  ID           NUMBER PRIMARY KEY,
  BLOB_CONTENT BLOB,
  CLOB_CONTENT CLOB
);

---------------------------------

CREATE SEQUENCE SEQ_USER_ID
INCREMENT BY 1 START WITH 1 MINVALUE 1 MAXVALUE 99999999 CYCLE );

---------------------------------

CREATE OR REPLACE PROCEDURE add_one(
  value  IN  NUMBER,
  result OUT NUMBER
) AS
  BEGIN
    result := value + 1;
  END;