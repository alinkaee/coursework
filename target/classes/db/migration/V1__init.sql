insert into roles (name)
values
('ROLE_USER')

insert into users (username, password, email)
values
('admin', '$2a$10$36VqC0u1DWcrx8uUMOadZeyJsFIkgeyHheEWzvMKZQjnFdBBDTu5G', 'admin@gmail.com');

insert into users_roles (user_id, role_id)
values
(1, 1),
(2, 2);

select * from "public".users;