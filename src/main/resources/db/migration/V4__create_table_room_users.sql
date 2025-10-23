create table room_users(id serial not null primary key,
                        user_id int not null,
                        room_id int not null,
                        foreign key(user_id) references users(id) on delete cascade,
                        foreign key(room_id) references rooms(id) on delete cascade);