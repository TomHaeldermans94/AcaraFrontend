/* gebruik hier de online bcrypt encoder! https://bcrypt-generator.com/ */
insert into user(id, username, password, first_name, last_name)
values (1, 'admin', '$2y$10$14ux7BShikxkMJ2.qVyXOOIur8Q0Br58RzfdxYpC9k5DqEXqzEdx.', 'admin', 'admin'); /* admin admin */
insert into user(id, username, password, first_name, last_name)
values (2, 'ruben', '$2y$10$7D8fk.KuQGuTjxsYOcAnUePD.eph.fSJyGOOZ3DJuNy0l2OBL39JS', 'ruben',
        'neven'); /* ruben rubenpw */
insert into user(id, username, password, first_name, last_name)
values (3, 'tom', '$2y$10$r9vGBv4yyCGlr6mhfENLf.QVaPiPtKf2CL2aQhsFe2KIZ6TqV.CKe', 'tom', 'haeldermans'); /* tom tompw */
insert into user(id, username, password, first_name, last_name)
values (4, 'michiel', '$2a$09$NXc2Hlu9ng/rcjMHa4ggZO5J6ZXjZHtQZC4Ar360cJNIMVSW28K42', 'michiel',
        'thomassen'); /* michiel michielpw */
insert into user(id, username, password, first_name, last_name)
values (5, 'test', '$2y$10$mUDfNQFM4HXQzgW98NXD6eS.CxHjVkRq2E20ZT.0YM2zJu1AsJblm', 'test', 'test'); /* test test */

insert into role(id, name)
values (1, 'ROLE_ADMIN');
insert into role(id, name)
values (2, 'ROLE_USER');

insert into user_roles(users_id, roles_id)
values (1, 1);
insert into user_roles(users_id, roles_id)
values (1, 2);
insert into user_roles(users_id, roles_id)
values (4, 1);
insert into user_roles(users_id, roles_id)
values (4, 2);
insert into user_roles(users_id, roles_id)
values (5, 2);
insert into user_roles (users_id, roles_id)
values (2, 1);
insert into user_roles (users_id, roles_id)
values (3, 1);

insert into cart(id, user_id, total)
values (1, 1, 40);
insert into cart_item (id, amount, cart_id, event_id, item_total)
values (1, 2, 1, 1, 40);
insert into cart_items (cart_id, items_id)
values (1, 1);
