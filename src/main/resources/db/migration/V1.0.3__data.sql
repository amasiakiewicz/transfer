insert into team(id, version, created_date_time, commission_rate, funds_currency, funds_amount)
values ('b154fcf4-6884-4011-969c-319df208612e', 1, '2018-02-06 16:27:26.64243+00', 0.05, 'USD', 282343123.43);
insert into team(id, version, created_date_time, commission_rate, funds_currency, funds_amount)
values ('b254fcf4-6884-4011-969c-319df208612e', 1, '2018-02-07 16:27:26.64243+00', 0.05, 'CAD', 587211083.22);
insert into team(id, version, created_date_time, commission_rate, funds_currency, funds_amount)
values ('b354fcf4-6884-4011-969c-319df208612e', 1, '2018-02-08 16:27:26.64243+00', 0.05, 'PLN', 21392018329.93);

insert into player(id, version, created_date_time, team_id, date_of_birth, play_start)
values ('c154fcf4-6884-4011-969c-319df208612f', 1, '2018-02-07 16:27:26.64243+00', 'b154fcf4-6884-4011-969c-319df208612e', '1998-08-13',
        '2015-03-04');
insert into player(id, version, created_date_time, team_id, date_of_birth, play_start)
values ('c254fcf4-6884-4011-969c-319df208612f', 1, '2018-02-08 16:27:26.64243+00', 'b154fcf4-6884-4011-969c-319df208612e', '1997-10-21',
        '2010-02-11');
insert into player(id, version, created_date_time, team_id, date_of_birth, play_start)
values ('c354fcf4-6884-4011-969c-319df208612f', 1, '2018-02-09 16:27:26.64243+00', 'b154fcf4-6884-4011-969c-319df208612e', '1988-11-15',
        '2008-10-12');
insert into player(id, version, created_date_time, team_id, date_of_birth, play_start)
values ('c454fcf4-6884-4011-969c-319df208612f', 1, '2018-02-10 16:27:26.64243+00', 'b254fcf4-6884-4011-969c-319df208612e', '1977-08-13',
        '2003-12-10');
insert into player(id, version, created_date_time, team_id, date_of_birth, play_start)
values ('c554fcf4-6884-4011-969c-319df208612f', 1, '2018-02-11 16:27:26.64243+00', 'b254fcf4-6884-4011-969c-319df208612e', '1978-06-13',
        '2002-05-16');
insert into player(id, version, created_date_time, team_id, date_of_birth, play_start)
values ('c654fcf4-6884-4011-969c-319df208612f', 1, '2018-02-12 16:27:26.64243+00', 'b354fcf4-6884-4011-969c-319df208612e', '1888-10-03',
        '2008-07-17');

insert into exchange_rate(id, version, created_date_time, currency, rate, date)
values ('a154fcf4-6884-4011-969c-319df208612e', 1, '2017-02-06 16:27:26.64243+00', 'CAD', 1.556, '2020-09-16 16:27:26.64243+00');
insert into exchange_rate(id, version, created_date_time, currency, rate, date)
values ('a254fcf4-6884-4011-969c-319df208612e', 1, '2017-02-06 16:27:26.64243+00', 'USD', 1.1634, '2020-09-16 16:27:26.64243+00');
insert into exchange_rate(id, version, created_date_time, currency, rate, date)
values ('a354fcf4-6884-4011-969c-319df208612e', 1, '2017-02-06 16:27:26.64243+00', 'PLN', 4.5557, '2020-09-16 16:27:26.64243+00');
insert into exchange_rate(id, version, created_date_time, currency, rate, date)
values ('a454fcf4-6884-4011-969c-319df208612e', 1, '2017-02-06 16:27:26.64243+00', 'EUR', 1.0, '2020-09-16 16:27:26.64243+00');
insert into exchange_rate(id, version, created_date_time, currency, rate, date)
values ('a554fcf4-6884-4011-969c-319df208612e', 1, '2017-02-06 16:27:26.64243+00', 'JPY', 122.74, '2020-09-16 16:27:26.64243+00');
