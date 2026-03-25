create table cities
(
    id   serial
        primary key,
    name varchar(50) not null
);

create table profiles
(
    id  serial
        primary key,
    bio varchar(500) default ''::character varying not null
);

create table roles
(
    id   serial
        primary key,
    name varchar(100) not null
);

create table users
(
    id         serial
        primary key,
    name       varchar(255)                               not null,
    email      varchar(255) default ''::character varying not null,
    age        integer      default 0                     not null,
    city_id    integer
        constraint fk_users_city_id__id
            references cities
            on update restrict on delete restrict,
    profile_id integer
        constraint fk_users_profile_id__id
            references profiles
            on update restrict on delete restrict
);

create table userroles
(
    user_id integer not null
        constraint fk_userroles_user_id__id
            references users
            on update restrict on delete restrict,
    role_id integer not null
        constraint fk_userroles_role_id__id
            references roles
            on update restrict on delete restrict,
    constraint pk_userroles
        primary key (user_id, role_id)
);

CREATE TABLE books
(
    book_id SERIAL,
    title   varchar(255) NOT NULL,
    author  varchar(255) NOT NULL,
    isbn    varchar(255) NOT NULL,
    year    integer      NOT NULL,
    CONSTRAINT books_pkey PRIMARY KEY (book_id)
);

CREATE TABLE orders
(
    order_id SERIAL,
    user_id  integer NOT NULL,
    book_id  integer NOT NULL,
    quantity integer NOT NULL,
    CONSTRAINT orders_pkey PRIMARY KEY (order_id),
    CONSTRAINT orders_book_id_fkey FOREIGN KEY (book_id)
        REFERENCES books (book_id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT orders_user_id_fkey FOREIGN KEY (user_id)
        REFERENCES users (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);

insert into users(name, email, age)
values ('Aleksandr', 'alx@example.com', 33)