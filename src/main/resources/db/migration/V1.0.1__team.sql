create table team
(
    id                uuid primary key,
    version           int                      not null,
    created_date_time timestamp with time zone not null,
    commission_rate   numeric(5, 4)            not null,
    funds_currency    varchar(3)               not null,
    funds_amount      numeric(19, 2)           not null
);
