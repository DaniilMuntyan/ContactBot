DROP SCHEMA public CASCADE;
CREATE SCHEMA public;

DROP TABLE IF EXISTS "user";
create table "user"(
                       user_id SERIAL,
                       chat_id integer unique,
                       firstname varchar(100),
                       lastname varchar(100),
                       username varchar(100),
                       admin_mode boolean,
                       created_at timestamp with time zone not null default now(),
                       last_action timestamp with time zone,
                       primary key(user_id)
);
create index on "user"(chat_id);

drop table if exists contacts;
create table "contacts"(
                           id serial,
                           phone char(50),
                           name text,
                           created_at timestamp with time zone not null default now(),
                           updated_at timestamp with time zone not null default now(),
                           created_by integer references "user"(user_id) not null,
                           updated_by integer references "user"(user_id) not null,
                           PRIMARY KEY(id)
);

DROP TABLE IF EXISTS "new";
create table "new"(
                      id SERIAL,
                      phone char(50) UNIQUE,
                      created_at timestamp with time zone not null default now(),
                      created_by integer references "user"(user_id) not null,
                      primary key(id)
);