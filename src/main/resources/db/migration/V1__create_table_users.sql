create table users(id serial not null primary key,
                  name varchar(128) not null,
                  email varchar(1289) not null,
                  password varchar(512) not null)