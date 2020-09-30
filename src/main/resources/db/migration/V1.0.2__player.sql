create table player
(
    id                uuid primary key,
    version           int                      not null,
    created_date_time timestamp with time zone not null,
    team_id           uuid                     not null,
    date_of_birth     date                     not null,
    play_start        date                     not null
);
