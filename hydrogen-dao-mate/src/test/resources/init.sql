create table user_operation_log(
  user_id           varchar(40)     not null,
  operation_time    datetime        not null    default current_timestamp,
  operation         text            not null,
  denied            tinyint         not null    default 0
);
