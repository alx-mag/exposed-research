insert into cities(name)
values ('Samara'),
       ('Moscow'),
       ('Saint Petersburg'),
       ('Kazan'),
       ('Novosibirsk');

insert into roles(name)
values ('USER'),
       ('ADMIN'),
       ('EDITOR');

insert into profiles(bio)
select 'Bio for mock user #' || gs
from generate_series(1, 3000) as gs;

insert into users(name, email, age, city_id, profile_id)
select 'User ' || gs,
       'user' || gs || '@example.com',
       18 + (gs % 43),
       1 + ((gs - 1) % 5),
       gs
from generate_series(1, 3000) as gs;

insert into userroles(user_id, role_id)
select u.id,
       1
from users u
where u.email like 'user%@example.com';

insert into userroles(user_id, role_id)
select u.id,
       2
from users u
where u.email like 'user%@example.com'
  and u.id % 10 = 0;

insert into userroles(user_id, role_id)
select u.id,
       3
from users u
where u.email like 'user%@example.com'
  and u.id % 5 = 0;
