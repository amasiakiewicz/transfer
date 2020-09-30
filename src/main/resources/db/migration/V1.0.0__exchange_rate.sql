create sequence hibernate_sequence start 1 increment 1;

create table exchange_rate
(
    id                uuid primary key,
    version           int                      not null,
    created_date_time timestamp with time zone not null,
    currency          varchar(3)               not null unique,
    rate              numeric(12, 4)           not null,
    date              timestamp with time zone not null
);
