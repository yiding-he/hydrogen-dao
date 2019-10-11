create table if not exists payments(
  id identity primary key,
  pay_time timestamp default current_timestamp,
  amount int default 0
);
create index if not exists payments_pay_time
on payments(pay_time);
