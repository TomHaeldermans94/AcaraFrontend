insert into user(id, username, password) values (4,'michiel','$2a$09$NXc2Hlu9ng/rcjMHa4ggZO5J6ZXjZHtQZC4Ar360cJNIMVSW28K42');
insert into user(id, username, password) values (5,'test','$2y$10$mUDfNQFM4HXQzgW98NXD6eS.CxHjVkRq2E20ZT.0YM2zJu1AsJblm');

insert into role(id, name) values (1, 'ADMIN');
insert into role(id, name) values (2, 'USER');

insert into user_roles(users_id, roles_id) values(4,1);
insert into user_roles(users_id, roles_id) values(4,2);
insert into user_roles(users_id, roles_id) values(5,2);
