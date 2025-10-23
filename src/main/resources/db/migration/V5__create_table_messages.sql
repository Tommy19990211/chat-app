create table messages(id serial not null primary key,
                      content varchar(512),
                      user_id int not null,
                      room_id int not null,
                      image varchar(512),
                      created_at timestamp not null default current_timestamp,
                      foreign key(user_id)references users(id) on delete cascade,
                      foreign key(room_id)references rooms(id) on delete cascade
                      )