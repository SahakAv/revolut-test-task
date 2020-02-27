create table ACCOUNT
  (
      id            varchar(64)
          constraint account_pk primary key,
      ownerName     VARCHAR(64) not null,
      balance       double      not null,
      blockedAmount double      not null,
      currency      VARCHAR(5)  not null
  );
create table TRANSACTION
(
    id       varchar(64)
        constraint transaction_pk primary key,
    fromId   VARCHAR(64) not null,
    toId     VARCHAR(64) not null,
    amount   double not null,
    currency VARCHAR(5)  not null

);


